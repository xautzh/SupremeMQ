package com.xaut.client.transport;

import com.xaut.client.consumer.SupremeMQMessageConsumer;
import com.xaut.common.constant.MessageProperty;
import com.xaut.common.constant.MessageType;

import com.xaut.common.message.bean.SupremeMQMessage;
import com.xaut.common.utils.DateUtils;
import com.xaut.common.utils.MessageIdUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息分发和消息发送类
 */
public class MessageDispatcher {
    //key-消费者id value-消费者
    private ConcurrentHashMap<String, SupremeMQMessageConsumer> consumerMap =
            new ConcurrentHashMap<>();
    //消费者 应答map key-发送消息的id value-应答成功闭锁
    private ConcurrentHashMap<String, CountDownLatch> messageAckMap =
            new ConcurrentHashMap<>();
    //消费者 注册map key-客户端设定的消费者ID,value-消息应答成功的闭锁
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

    /**
     * 开始工作
     */
    public void start() {
        synchronized (isStarted) {
            if (isStarted.get()) {
                logger.info("MessageDispatcher已经启动......");
            }
            logger.info("MessageDispatcher准备开始工作... ...");
            threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            dispatcherThread = new Thread(() -> {
                Message message = null;
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        logger.debug("当前队列中内容为【{}】",receiveMessageQueue.size());
                        message = receiveMessageQueue.take();

                    } catch (InterruptedException e) {
                        logger.info("MessageDispatcher被中断，即将退出.");
                        break;
                    }
                    logger.debug("开始处理消息【{}】", message);

                    try {
                        //生产者 应答消息
                        if (MessageType.PRODUCER_ACKNOWLEDGE_MESSAGE.getValue().equals(message.getJMSType())) {
                            logger.debug("客户端接收到服务端的生产者应答消息:{}", message);
                            CountDownLatch countDownLatch = messageAckMap.get(message.getJMSMessageID());
                            if (countDownLatch != null) {
                                countDownLatch.countDown();
                                messageAckMap.remove(message.getJMSMessageID());
                            }
                        }
                        //要分配给消费者的消息
                        else if (MessageType.PRODUCER_MESSAGE.getValue().equals(message.getJMSType())) {
                            logger.debug("客户端接收到要分配给消费者的消息:{}", message);
                            String consumerId = message.getStringProperty(MessageProperty.CUSTOMER_ID.getKey());
                            if (StringUtils.isBlank(consumerId)) {
                                logger.error("客户端接收到生产者发来的消息中消费者ID为空，分配消息到消费者失败:{}", message);
                                continue;
                            }
                            //放入指定消费者队列中
                            logger.debug("消息【{}】已放入队列",message);
                            SupremeMQMessageConsumer supremeMQMessageConsumer = consumerMap.get(consumerId);
                            supremeMQMessageConsumer.putMessage(message);

                        }
                        //消费者注册 应答消息
                        else if (MessageType.CUSTOMER_REGISTER_ACKNOWLEDGE_MESSAGE.getValue().equals(message.getJMSType())) {
                            logger.debug("客户端接收到服务器发来的发来消费者注册应答消息:{}", message);
                            String customerId = message.getStringProperty(MessageProperty.CUSTOMER_ID.getKey());
                            String customerClientId = message.getStringProperty(MessageProperty.CUSTOMER_CLIENT_ID.getKey());
                            if (StringUtils.isBlank(customerId) || StringUtils.isBlank(customerClientId)) {
                                logger.error("消费者注册应答消息数据不完整，处理失败【{}】", message);
                            }
                            DataCountDownLatch<Message> countDownLatch = addConsumerAckMap.get(customerClientId);
                            countDownLatch.setData(message);
                            countDownLatch.countDown();
                        } else {
                            logger.error("未知消息类型，无法处理【{}】", message);
                        }
                    } catch (JMSException e) {
                        logger.error("SugarMQQueueDispatcher消息处理失败:【{}】", message, e);
                    }

                }
            });
            dispatcherThread.start();

            logger.info("MessageDispatcher已经开始工作");

            isStarted.set(true);
        }
    }

    /**
     * 关闭消息分发器
     */
    public void closed(){
        synchronized (isClosed) {
            if(isClosed.get()) {
                return ;
            } else {
                isClosed.set(true);
            }
        }

        logger.info("MessageDispatcher即将关闭... ...");

        if(threadPoolExecutor != null) {
            threadPoolExecutor.shutdown();
        }

        if(dispatcherThread != null) {
            dispatcherThread.interrupt();
        }

        logger.info("MessageDispatcher已经关闭！");
    }

    /**
     * 同步发送生产者消息
     *
     * @param message 消息
     * @throws JMSException
     */

    public void sendMessage(Message message) throws JMSException {
        if (message == null) {
            throw new IllegalArgumentException("Message不能为空！");
        }

        if (!isStarted.get()) {
            logger.error("MessageDispatcher还未启动，消息发送失败：【{}】", message);
        }

        String messageId = MessageIdUtils.getNewMessageId();
        message.setJMSMessageID(messageId);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        messageAckMap.put(messageId, countDownLatch);

        try {
            logger.debug("前【{}】",sendMessageQueue.size());
            sendMessageQueue.put(message);
            logger.debug("后【{}】",sendMessageQueue.size());
            countDownLatch.await();
            messageAckMap.remove(messageId);
        } catch (InterruptedException e) {
            logger.error("SupremeMQMessageProducer消息发送被中断！");
            throw new JMSException("SupremeMQMessageProducer消息发送被中断:" + e.getMessage());
        }

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

    public BlockingQueue<Message> getSendMessageQueue() {
        return sendMessageQueue;
    }

    /**
     * 类说明：带有消息负载的闭锁
     * <p>
     * 类描述：主要用于阻塞发送后返回应答消息
     *
     *
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
