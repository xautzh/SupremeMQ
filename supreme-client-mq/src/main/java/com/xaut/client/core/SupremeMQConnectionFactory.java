package com.xaut.client.core;

import com.xaut.client.transport.SupremeMQTransport;
import com.xaut.server.transport.SupremeMQTransportFactory;

import javax.jms.*;

public class SupremeMQConnectionFactory implements ConnectionFactory, QueueConnectionFactory, TopicConnectionFactory {
    //客户端传送器接口
    private SupremeMQTransport supremeMQTransport;

    @Override
    public QueueConnection createQueueConnection() throws JMSException {

        return null;
    }

    /**
     * 通过ip port 创建SupremeMQConnectionFactory对象
     *
     * @return
     * @throws JMSException
     */
    public SupremeMQConnectionFactory(String providerUrl) throws JMSException {
        //通过ip port 创建SupremeMQConnectionFactory对象
        this.supremeMQTransport = SupremeMQTransportFactory.createSupremeMQTransport(providerUrl);
    }

    @Override
    public QueueConnection createQueueConnection(String s, String s1) throws JMSException {
        return null;
    }

    @Override
    public TopicConnection createTopicConnection() throws JMSException {
        return null;
    }

    @Override
    public TopicConnection createTopicConnection(String s, String s1) throws JMSException {
        return null;
    }


    @Override
    public Connection createConnection() throws JMSException {
        //创建连接
        return new SuprmeMQConnection(supremeMQTransport);
    }

    @Override
    public Connection createConnection(String s, String s1) throws JMSException {
        return null;
    }
}
