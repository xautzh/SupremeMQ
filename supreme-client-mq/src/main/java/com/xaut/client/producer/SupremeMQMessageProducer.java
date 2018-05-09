package com.xaut.client.producer;

import com.xaut.client.transport.MessageDispatcher;
import com.xaut.common.constant.MessageProperty;
import com.xaut.common.constant.MessageType;
import com.xaut.common.message.bean.SupremeMQMessage;
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
        this.disableMessageId.set(b);

    }

    @Override
    public boolean getDisableMessageID() throws JMSException {
        return this.disableMessageId.get();
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
        this.priority.set(i);

    }

    @Override
    public int getPriority() throws JMSException {
        return this.priority.get();
    }

    @Override
    public void setTimeToLive(long l) throws JMSException {
        this.timeToLive.set(l);

    }

    @Override
    public long getTimeToLive() throws JMSException {
        return this.timeToLive.get();
    }

    @Override
    public Destination getDestination() throws JMSException {
        return this.destination;
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
        logger.debug("即将发送一条消息:{}", message);
        message.setJMSType(MessageType.PRODUCER_MESSAGE.getValue()); // 设置消息类型
        message.setJMSDestination(destination);
        message.setBooleanProperty(MessageProperty.DISABLE_MESSAGE_ID.getKey(), disableMessageId.get());
        messageDispatcher.sendMessage((SupremeMQMessage)message);

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
