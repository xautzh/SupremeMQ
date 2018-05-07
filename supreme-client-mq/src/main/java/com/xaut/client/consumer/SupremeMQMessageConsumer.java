package com.xaut.client.consumer;

import javax.jms.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SupremeMQMessageConsumer implements MessageConsumer{
    private String consumerId;	// 消费者ID，可以由客户端设置，但最终由服务端来决定

    private String state;	// 消费者的状态

    private String messageSelector;
    private Destination destination;

    private Runnable consumeMessageTask;
    private Runnable ackMessageTask;

    // 未消费的消息队列
    private BlockingQueue<Message> messageQueue;
    // 已消费但还未应答的消息队列
    private BlockingQueue<Message> ackMessageQueue;

    private BlockingQueue<Message> sendMessageQueue;

    private MessageListener messageListener;

    private AtomicBoolean isStarted = new AtomicBoolean(false);

    @Override
    public String getMessageSelector() throws JMSException {
        return null;
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return null;
    }

    @Override
    public void setMessageListener(MessageListener messageListener) throws JMSException {

    }

    @Override
    public Message receive() throws JMSException {
        return null;
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

    }
}
