package com.xaut.client.core;

import com.xaut.client.producer.SupremeMQMessageProducer;
import com.xaut.client.transport.MessageDispatcher;
import com.xaut.server.message.SupremeMQDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Serializable;

public class SupremeMQSession implements Session{
    private MessageDispatcher messageDispatcher;

    private Logger logger = LoggerFactory.getLogger(SupremeMQSession.class);
    @Override
    public BytesMessage createBytesMessage() throws JMSException {
        return null;
    }

    @Override
    public MapMessage createMapMessage() throws JMSException {
        return null;
    }

    @Override
    public Message createMessage() throws JMSException {
        return null;
    }

    @Override
    public ObjectMessage createObjectMessage() throws JMSException {
        return null;
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable serializable) throws JMSException {
        return null;
    }

    @Override
    public StreamMessage createStreamMessage() throws JMSException {
        return null;
    }

    @Override
    public TextMessage createTextMessage() throws JMSException {
        return null;
    }

    @Override
    public TextMessage createTextMessage(String s) throws JMSException {
        return null;
    }

    @Override
    public boolean getTransacted() throws JMSException {
        return false;
    }

    @Override
    public int getAcknowledgeMode() throws JMSException {
        return 0;
    }

    @Override
    public void commit() throws JMSException {

    }

    @Override
    public void rollback() throws JMSException {

    }

    @Override
    public void close() throws JMSException {

    }

    @Override
    public void recover() throws JMSException {

    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return null;
    }

    @Override
    public void setMessageListener(MessageListener messageListener) throws JMSException {

    }

    @Override
    public void run() {

    }

    /**
     * 创建生产者
     * @param destination
     * @return
     * @throws JMSException
     */

    @Override
    public MessageProducer createProducer(Destination destination) throws JMSException {
        if(!(destination instanceof SupremeMQDestination)) {
            logger.warn("传入的Destination非法！");
            throw new JMSException("传入的Destination非法！");
        }

        SupremeMQMessageProducer supremeMQMessageProducer = new SupremeMQMessageProducer(destination, messageDispatcher);
        return supremeMQMessageProducer;
    }

    /**
     * 创建消费者
     * @param destination
     * @return
     * @throws JMSException
     */

    @Override
    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        return null;
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String s) throws JMSException {
        return null;
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String s, boolean b) throws JMSException {
        return null;
    }

    @Override
    public Queue createQueue(String s) throws JMSException {
        return null;
    }

    @Override
    public Topic createTopic(String s) throws JMSException {
        return null;
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String s) throws JMSException {
        return null;
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String s, String s1, boolean b) throws JMSException {
        return null;
    }

    @Override
    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        return null;
    }

    @Override
    public QueueBrowser createBrowser(Queue queue, String s) throws JMSException {
        return null;
    }

    @Override
    public TemporaryQueue createTemporaryQueue() throws JMSException {
        return null;
    }

    @Override
    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return null;
    }

    @Override
    public void unsubscribe(String s) throws JMSException {

    }
}
