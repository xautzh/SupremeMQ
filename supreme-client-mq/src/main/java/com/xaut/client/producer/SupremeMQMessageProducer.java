package com.xaut.client.producer;

import com.xaut.client.transport.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SupremeMQMessageProducer implements MessageProducer {
    // 持久性和非持久性
    protected volatile AtomicInteger deliveryMode = new AtomicInteger(Message.DEFAULT_DELIVERY_MODE);
    // 消息有效期
    protected volatile AtomicLong timeToLive = new AtomicLong(Message.DEFAULT_TIME_TO_LIVE);
    // 消息优先级
    protected volatile AtomicInteger priority = new AtomicInteger(Message.DEFAULT_PRIORITY);

    protected volatile AtomicBoolean disableMessageId = new AtomicBoolean(false);
    private Destination destination;
    private MessageDispatcher messageDispatcher;


    private Logger logger = LoggerFactory.getLogger(SupremeMQMessageProducer.class);

    public SupremeMQMessageProducer(Destination destination, MessageDispatcher messageDispatcher) {
        this.destination = destination;
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void setDisableMessageID(boolean b) throws JMSException {

    }

    @Override
    public boolean getDisableMessageID() throws JMSException {
        return false;
    }

    @Override
    public void setDisableMessageTimestamp(boolean b) throws JMSException {

    }

    @Override
    public boolean getDisableMessageTimestamp() throws JMSException {
        return false;
    }

    @Override
    public void setDeliveryMode(int i) throws JMSException {

    }

    @Override
    public int getDeliveryMode() throws JMSException {
        return 0;
    }

    @Override
    public void setPriority(int i) throws JMSException {

    }

    @Override
    public int getPriority() throws JMSException {
        return 0;
    }

    @Override
    public void setTimeToLive(long l) throws JMSException {

    }

    @Override
    public long getTimeToLive() throws JMSException {
        return 0;
    }

    @Override
    public Destination getDestination() throws JMSException {
        return null;
    }

    @Override
    public void close() throws JMSException {

    }

    /**
     * 发送消息
     *
     * @param message
     * @throws JMSException
     */

    @Override
    public void send(Message message) throws JMSException {

    }

    @Override
    public void send(Message message, int i, int i1, long l) throws JMSException {

    }

    @Override
    public void send(Destination destination, Message message) throws JMSException {

    }

    @Override
    public void send(Destination destination, Message message, int i, int i1, long l) throws JMSException {

    }
}
