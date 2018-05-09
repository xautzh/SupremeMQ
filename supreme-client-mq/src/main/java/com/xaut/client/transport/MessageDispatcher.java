package com.xaut.client.transport;

import com.xaut.client.consumer.SupremeMQMessageConsumer;
import com.xaut.common.constant.MessageProperty;
import com.xaut.common.constant.MessageType;
import com.xaut.common.message.bean.SupremeMQMessage;
import com.xaut.common.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息分发和消息发送类
 */
public class MessageDispatcher {
    //key-消费者id value-消费者
    private ConcurrentHashMap<String, SupremeMQMessageConsumer> consumerMap =
            new ConcurrentHashMap<>();
    //消费应答map key-发送消息的id value-应答成功闭锁
    private ConcurrentHashMap<String, CountDownLatch> messageAckMap =
            new ConcurrentHashMap<>();
    //消费注册map key-客户端设定的消费者ID,value-消息应答成功的闭锁
    private ConcurrentHashMap<String, DataCountDownLatch<Message>> addConsumerAckMap =
            new ConcurrentHashMap<>();
    //待分发的消息队列
    private BlockingQueue<Message> receiveMessageQueue;
    //发送消息的队列
    private BlockingQueue<Message> sendMessageQueue;
    private Thread dispatcherThread;
    private ThreadPoolExecutor threadPoolExecutor;
    private AtomicBoolean isStarted = new AtomicBoolean(false);
    private AtomicBoolean isClosed = new AtomicBoolean(false);
    private Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);

    /**
     * 构造器
     *
     * @param receiveMessageQueue 待分发的消息队列
     * @param sendMessageQueue    发送消息队列
     */
    public MessageDispatcher(BlockingQueue<Message> receiveMessageQueue, BlockingQueue<Message> sendMessageQueue) {
        this.receiveMessageQueue = receiveMessageQueue;
        this.sendMessageQueue = sendMessageQueue;
    }

    public void sendMessage(Message message) {


    }

    public synchronized void addConsumer(MessageConsumer messageConsumer) throws JMSException {
        logger.info("准备注册一个消费者：{}", messageConsumer);
        if (messageConsumer == null || !(messageConsumer instanceof SupremeMQMessageConsumer)) {
            throw new IllegalArgumentException("messageConsumer为空或类型错误！");
        }
        SupremeMQMessageConsumer consumer = (SupremeMQMessageConsumer) messageConsumer;
        String consumerId = consumer.getConsumerId();
        if (StringUtils.isBlank(consumerId) || addConsumerAckMap.containsKey(consumerId)) {
            consumerId = getNewCustomerId();
        }
        Message addConsumerMsg = new SupremeMQMessage();
        addConsumerMsg.setStringProperty(MessageProperty.CUSTOMER_CLIENT_ID.getKey(), consumerId);
        addConsumerMsg.setJMSType(MessageType.CUSTOMER_REGISTER_MESSAGE.getValue());
        addConsumerMsg.setJMSDestination(consumer.getDestination());

        DataCountDownLatch<Message> dataCountDownLatch = new DataCountDownLatch<Message>(1);
        logger.debug("将即将注册的消费者放入addConsumerAckMap：【{}】", messageConsumer);
        addConsumerAckMap.put(consumerId, dataCountDownLatch);

        try {
            logger.debug("将消费者注册消息放入发送队列：【{}】", addConsumerMsg);
            sendMessageQueue.put(addConsumerMsg);
            dataCountDownLatch.await();
        } catch (InterruptedException e) {
            logger.info("靠，有必要吗？注册个消费者你也要中断？");
        }

        Message ackMsg = dataCountDownLatch.getData();
        logger.debug("消费者注册消息得到应答：【{}】", ackMsg);
        consumerId = ackMsg.getStringProperty(MessageProperty.CUSTOMER_ID.getKey());
        consumer.setConsumerId(consumerId);
        consumerMap.put(consumerId, consumer);

        addConsumerAckMap.remove(ackMsg.getStringProperty(MessageProperty.CUSTOMER_ID.getKey()));

        logger.info("成功注册了一个消费者：{}", messageConsumer);

    }

    /**
     * 类说明：带有消息负载的闭锁
     * <p>
     * 类描述：主要用于阻塞发送后返回应答消息
     *
     * @author manzhizhen
     * <p>
     * 2014年12月17日
     */
    class DataCountDownLatch<T> extends CountDownLatch {
        private T data;

        public DataCountDownLatch(int count) {
            super(count);
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    /**
     * 生成一个消费者ID
     * 非线程安全
     *
     * @return
     */
    private String getNewCustomerId() {
        String newId = DateUtils.formatDate(DateUtils.DATE_FORMAT_TYPE2);
        Random random = new Random(new Date().getTime());
        int next = random.nextInt(1000000);
        while (true) {
            if (addConsumerAckMap.containsKey(newId + next)) {
                next = random.nextInt(1000000);
            } else {
                break;
            }
        }

        logger.debug("生成的消费者ID为【{}】", newId + next);
        return newId + next;
    }
}
