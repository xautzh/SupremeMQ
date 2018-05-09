package com.xaut.client.consumer;

import com.xaut.common.constant.ConsumerState;
import com.xaut.common.constant.MessageProperty;
import com.xaut.common.constant.MessageType;
import com.xaut.common.message.bean.SupremeMQMessage;
import com.xaut.common.utils.MessageIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SupremeMQMessageConsumer implements MessageConsumer {
    private String consumerId;    // 消费者ID，可以由客户端设置，但最终由服务端来决定

    private String state;    // 消费者的状态

    private String messageSelector;
    private Destination destination;

    private Runnable consumeMessageTask;
    private Runnable ackMessageTask;

    // 未消费的消息队列
    private BlockingQueue<Message> messageQueue;
    // 已消费但还未应答的消息队列
    private BlockingQueue<Message> ackMessageQueue;
    //SupremeMQTransport中的消息发送队列
    private BlockingQueue<Message> sendMessageQueue;

    private MessageListener messageListener;

    private AtomicBoolean isStarted = new AtomicBoolean(false);

    private Logger logger = LoggerFactory.getLogger(SupremeMQMessageConsumer.class);

    /**
     * 通过构造函数创建消费者
     *
     * @param destination      目的地
     * @param sendMessageQueue SupremeMQTransport中的sendMessageQueue
     * @param cacheSize        缓存大小
     */
    public SupremeMQMessageConsumer(Destination destination, BlockingQueue<Message> sendMessageQueue, int cacheSize) {
        if (destination == null) {
            throw new IllegalArgumentException("创建消费者失败，Destination为空！");
        }

        if (cacheSize <= 0) {
            throw new IllegalArgumentException("创建消费者失败，cacheSize必须大于0！");
        }
        this.destination = destination;
        this.sendMessageQueue = sendMessageQueue;
        messageQueue = new LinkedBlockingQueue<Message>(cacheSize);
        ackMessageQueue = new LinkedBlockingQueue<Message>(cacheSize);
        consumeMessageTask = new ConsumeMessageTask(this);
        ackMessageTask = new AckMessageTask(this);
        state = ConsumerState.CREATE.getValue();
        logger.debug("新建立了一个消费者");
    }

    /**
     * 给消费者分配一条消息
     *
     * @param message 待分配的消息
     */
    public void putMessage(Message message) {
        if (ConsumerState.WORKING.getValue().equals(state)) {
            try {
                messageQueue.put(message);
            } catch (InterruptedException e) {
                logger.error("给消费者【{}】分配消息【{}】失败【{}】", this, message, e);
            }
        } else {
            logger.error("给消费者【{}】分配消息【{}】失败，消费者状态错误", this, message);
        }
    }

    @Override
    public String getMessageSelector() throws JMSException {
        return messageSelector;
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return messageListener;
    }

    @Override
    public void setMessageListener(MessageListener messageListener) throws JMSException {
        if (messageListener == null) {
            throw new JMSException("消息监听器不能为空！");
        }

        this.messageListener = messageListener;
    }

    @Override
    public Message receive() throws JMSException {
        return receive(0);
    }

    @Override
    public Message receive(long l) throws JMSException {
        return null;
    }

    @Override
    public Message receiveNoWait() throws JMSException {
        return null;
    }

    @Override
    public void close() throws JMSException {
        state = ConsumerState.DEATH.getValue();
        messageQueue.clear();
        messageQueue = null;

    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public BlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }

    public void setMessageQueue(BlockingQueue<Message> messageQueue) {
        this.messageQueue = messageQueue;
    }

    public BlockingQueue<Message> getAckMessageQueue() {
        return ackMessageQueue;
    }

    public void setAckMessageQueue(BlockingQueue<Message> ackMessageQueue) {
        this.ackMessageQueue = ackMessageQueue;
    }

    public BlockingQueue<Message> getSendMessageQueue() {
        return sendMessageQueue;
    }

    public void setSendMessageQueue(BlockingQueue<Message> sendMessageQueue) {
        this.sendMessageQueue = sendMessageQueue;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public Destination getDestination() {
        return destination;
    }

    /**
     * 开启一个消费者
     */
    public void start() {

    }

    /**
     * 消息消费线程
     */
    class ConsumeMessageTask implements Runnable {
        private SupremeMQMessageConsumer supremeMQMessageConsumer;

        private Logger logger = LoggerFactory.getLogger(ConsumeMessageTask.class);

        public ConsumeMessageTask(SupremeMQMessageConsumer supremeMQMessageConsumer) {
            this.supremeMQMessageConsumer = supremeMQMessageConsumer;
        }

        @Override
        public void run() {
            try {
                MessageListener messageListener = supremeMQMessageConsumer.getMessageListener();
                if (messageListener == null) {
                    logger.error("消费者{}没有配置消息监听器！", supremeMQMessageConsumer);
                    return;
                }
                BlockingQueue<Message> queue = supremeMQMessageConsumer.getMessageQueue();
                Message message = null;
                while (!Thread.currentThread().isInterrupted() &&
                        ConsumerState.WORKING.getValue().equals(supremeMQMessageConsumer.getState())) {
                    try {
                        message = queue.poll();
                        if (message == null) {
                            SupremeMQMessage pullMessage = new SupremeMQMessage();
                            pullMessage.setStringProperty(MessageProperty.CUSTOMER_ID.getKey(), consumerId);
                            pullMessage.setJMSMessageID(MessageIdUtils.getNewMessageId());
                            pullMessage.setJMSType(MessageType.CUSTOMER_MESSAGE_PULL.getValue());
                            pullMessage.setJMSDestination(message.getJMSDestination());
                            sendMessageQueue.put(pullMessage);
                            logger.debug("拉取消息【{}】已被放入发送队列。", pullMessage);
                            message = queue.take();
                        }
                        messageListener.onMessage(message);
                        supremeMQMessageConsumer.getAckMessageQueue().put(message);
                    } catch (InterruptedException e) {
                        logger.error("消费者【{}】消费消息线程被中断【{}】", supremeMQMessageConsumer, e);
                        break;
                    }
                }
            } catch (JMSException e) {
                logger.error("消费者【{}】消费消息失败：【{}】", supremeMQMessageConsumer, e);
            }
        }
    }

    /**
     * 消息应答线程
     */
    class AckMessageTask implements Runnable {
        private SupremeMQMessageConsumer supremeMQMessageConsumer;

        private Logger logger = LoggerFactory.getLogger(AckMessageTask.class);

        public AckMessageTask(SupremeMQMessageConsumer supremeMQMessageConsumer) {
            this.supremeMQMessageConsumer = supremeMQMessageConsumer;
        }

        @Override
        public void run() {
            Message message = null;
            while (!Thread.currentThread().isInterrupted() &&
                    ConsumerState.WORKING.getValue().equals(supremeMQMessageConsumer.getState())) {
                try {
                    message = supremeMQMessageConsumer.getAckMessageQueue().take();
                    //创建应答消息
                    SupremeMQMessage ackMessage = new SupremeMQMessage();
                    //设置属性
                    ackMessage.setJMSType(MessageType.CUSTOMER_ACKNOWLEDGE_MESSAGE.getValue());
                    ackMessage.setJMSCorrelationID(message.getJMSMessageID());
                    ackMessage.setJMSMessageID(MessageIdUtils.getNewMessageId());
                    ackMessage.setJMSDestination(message.getJMSDestination());
                    sendMessageQueue.put(message);
                    logger.debug("应答消息【{}】已被放入发送队列。", ackMessage);
                } catch (InterruptedException e) {
                    logger.debug("AckMessageTask 被中断");
                } catch (JMSException e) {
                    logger.error("【{}】发送应消息【{}】失败：【{}】", supremeMQMessageConsumer, message, e);
                }
            }

        }
    }
}
