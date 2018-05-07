package com.xaut.server.manager;


import com.xaut.common.constant.ConnectionProperty;
import com.xaut.common.constant.MessageProperty;
import com.xaut.common.constant.MessageType;
import com.xaut.common.message.Message;
import com.xaut.common.message.SupremeMQDestination;
import com.xaut.common.message.bean.SupremeMQMessage;
import com.xaut.common.utils.DateUtils;
import com.xaut.server.dispatch.SupremeMQConsumerDispatcher;
import com.xaut.server.queue.SupremeMQMessageContainer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 消费者管理器
 */
public class SupremeMQConsumerManager {
    //key-消费者id value-SupremeMQServerTransport的sendMessageQueue
    private ConcurrentHashMap<String, BlockingQueue<Message>> consumerMap =
            new ConcurrentHashMap<>();
    //key-目的地名称 value-消费者ID容器
    private ConcurrentHashMap<String, PollArray<String>> destinationMap =
            new ConcurrentHashMap<String, PollArray<String>>();
    //消息分发器
    private ConcurrentHashMap<String, SupremeMQConsumerDispatcher> consumerDispatcherMap =
            new ConcurrentHashMap<>();
    //一次性向消费者推送的消息数量
    private int clientMessageBatchSendAmount = (Integer) ConnectionProperty.CLIENT_MESSAGE_BATCH_ACK_QUANTITY.getValue();

    private Logger logger = LoggerFactory.getLogger(SupremeMQConsumerManager.class);

    //1.注册消费者
    //2.将消息推送到一个消费者的待发送队列
    //3/生成消费者id  可以使用Md5加密算法 或者random随机  推荐前者 唯一性较高

    public void addConsumer(Message message, BlockingQueue<Message> sendMessageQueue) throws JMSException {
        if (message == null || !MessageType.CUSTOMER_REGISTER_MESSAGE.getValue()
                .equals(message.getJMSType())) {
            throw new IllegalArgumentException();
        }
        String customerClientId = message.getStringProperty(MessageProperty.CUSTOMER_CLIENT_ID.getKey());
        String customerId = customerClientId;
        if (StringUtils.isBlank(customerId) || consumerMap.containsKey(customerId)) {
            logger.debug("客户端没有填写消费者ID【{}】", message);
            customerId = getNewCustomerId();
        }
        consumerMap.put(customerId, sendMessageQueue);
        PollArray<String> ergodicArray = destinationMap.putIfAbsent(
                ((SupremeMQDestination) message.getJMSDestination()).getQueueName(), new PollArray<String>(10));
        if (ergodicArray == null) {
            ergodicArray = destinationMap.get(((SupremeMQDestination) message.
                    getJMSDestination()).getQueueName());
        }

        ergodicArray.add(customerId);
        SupremeMQMessageContainer container = ((SupremeMQMessageContainer) message.getJMSDestination());
        SupremeMQConsumerDispatcher dispatcher = consumerDispatcherMap.putIfAbsent(
                container.getName(), new SupremeMQConsumerDispatcher(this, container));
        if (dispatcher == null) {
            logger.debug("该消费者监听的目的地还未配置消费者分发器【{}】", message);
            dispatcher = consumerDispatcherMap.get(container.getName());
            logger.debug("新建消费者分发器【{}】", dispatcher);
        }

        if (!dispatcher.isStart()) {
            dispatcher.start();
            logger.debug("消费者分发器启动成功【{}】", dispatcher);
        }
        // 应答消费者注册
        Message consumerAckMsg = new SupremeMQMessage();
        consumerAckMsg.setJMSType(MessageType.CUSTOMER_REGISTER_ACKNOWLEDGE_MESSAGE.getValue());
        consumerAckMsg.setStringProperty(MessageProperty.CUSTOMER_CLIENT_ID.getKey(), customerClientId);
        consumerAckMsg.setStringProperty(MessageProperty.CUSTOMER_ID.getKey(), customerId);
        try {
            sendMessageQueue.put(consumerAckMsg);
            logger.debug("将消费者注册应答消息放入发送队列【{}】", consumerAckMsg);
        } catch (InterruptedException e) {
            logger.error("将消费者注册应答消息放入发送队列被中断【{}】", consumerAckMsg);
        }
    }

    /**
     * 生成一个消费者Id
     *
     * @return
     */
    private String getNewCustomerId() {
        String newId = DateUtils.formatDate(DateUtils.DATE_FORMAT_TYPE2);
        Random random = new Random(new Date().getTime());
        int next = random.nextInt(1000000);
        while (true) {
            if (consumerMap.containsKey(newId + next)) {
                next = random.nextInt(1000000);
            } else {
                break;
            }
        }

        logger.debug("生成的消费者ID为【{}】", newId + next);
        return newId + next;
    }

    /**
     * 将消息推送到一个消费者的待发送队列中
     * @param message
     * @throws JMSException
     */
    public void putMessageToCustomerQueue(Message message) throws JMSException {
        if (message == null) {
            throw new IllegalArgumentException("Message不能为空！");
        }

        logger.debug("准备将消息推送到一个消费者的待发送队列中【{}】", message);

        SupremeMQMessageContainer sugarMQMessageContainer = (SupremeMQMessageContainer) message.getJMSDestination();
        PollArray<String> pollArray = destinationMap.putIfAbsent(sugarMQMessageContainer.getQueueName(), new PollArray<String>(10));

        String nextConsumerId;
        try {
            nextConsumerId = pollArray.getNext();
        } catch (InterruptedException e) {
            logger.error("获取下一个消费者ID失败", e);
            throw new JMSException(String.format("获取下一个消费者ID失败:{}", e));
        }

        BlockingQueue<Message> queue = consumerMap.get(nextConsumerId);
        message.setStringProperty(MessageProperty.CUSTOMER_ID.getKey(), nextConsumerId);
        SupremeMQDestination dest = (SupremeMQDestination) message.getJMSDestination();
        message.setJMSDestination(new SupremeMQDestination(dest.getName(), dest.getType()));
        try {
            queue.put(message);
            // 之所以给消费者推送消息设置阻塞开关，是为了防止消费者处理不过来造成消费者端消息堆积，这里暂时不设置阻塞
            updateConsumerState(sugarMQMessageContainer.getQueueName(), nextConsumerId, false);
            logger.debug("成功将消息【{}】推送到消费者【{}】队列！", message, nextConsumerId);
        } catch (InterruptedException e) {
            logger.error("将消息【{}】推送到消费者【{}】队列失败！", message, nextConsumerId);
        }
    }
    /**
     * 更新消费者的空闲状态
     *
     * @param queueName
     * @param consumerId
     * @param isIdel
     */
    public void updateConsumerState(String queueName, String consumerId, boolean isIdel) {
        if (!destinationMap.containsKey(queueName)) {
            logger.error("更新消费者【{}】状态失败，不存在的消息队列【{}】", consumerId, queueName);
            return;
        }

        destinationMap.get(queueName).setValue(consumerId, isIdel);
    }


    /**
     * 类说明：可按顺序遍历数组
     *
     * @param <T>
     */
    class PollArray<T> {
        private CopyOnWriteArrayList<Entry<T, Boolean>> contentArray = new CopyOnWriteArrayList<>();
        private BlockingQueue outputQueue;
        private AtomicBoolean isClosed = new AtomicBoolean(false);
        private Thread thread;

        public PollArray(int outputQueueSize) {
            if (outputQueueSize <= 0) {
                throw new IllegalArgumentException();
            }

            outputQueue = new LinkedBlockingQueue<T>(outputQueueSize);

            thread = new Thread(() -> {
                while (!isClosed.get()) {
                    Iterator<Entry<T, Boolean>> iterator = contentArray.iterator();
                    while (iterator.hasNext()) {
                        try {
                            if (!iterator.next().getValue()) {
                                continue;
                            }

                            outputQueue.put(iterator.next().getKey());
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                }
            });

            thread.start();
        }

        public T getNext() throws InterruptedException {
            return (T) outputQueue.take();
        }

        public T getNext(long time) throws InterruptedException {
            return (T) outputQueue.poll(time, TimeUnit.MILLISECONDS);
        }

        public void add(T t) {
            contentArray.addIfAbsent(new Entry<T, Boolean>(t, new Boolean(true)));
        }

        public void remove(T t) {
            contentArray.remove(t);
        }

        public boolean isEmpty() {
            return contentArray.isEmpty();
        }

        public void setValue(T t, Boolean isIdle) {
            for (Entry<T, Boolean> entry : contentArray) {
                if (entry.getKey().equals(t)) {
                    entry.setValue(isIdle);
                    break;
                }
            }
        }

        public void close() {
            isClosed.set(false);
            if (thread != null) {
                thread.interrupt();
            }
        }

        private class Entry<K, V> implements Map.Entry<K, V> {
            private K key;
            private V value;

            public Entry(K key, V value) {
                this.key = key;
                this.value = value;
            }

            @Override
            public K getKey() {
                return key;
            }

            @Override
            public V getValue() {
                return value;
            }

            @Override
            public V setValue(V value) {
                this.value = value;
                return this.value;
            }
        }
    }

    public void setClientMessageBatchSendAmount(int clientMessageBatchSendAmount) {
        this.clientMessageBatchSendAmount = clientMessageBatchSendAmount;
    }
}
