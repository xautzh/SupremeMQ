package com.xaut.client.core;

import com.xaut.client.transport.SupremeMQTransport;

import javax.jms.*;

public class SuprmeMQConnection implements Connection {
    //客户端传送接口
    private SupremeMQTransport supremeMQTransport;

    public SuprmeMQConnection(SupremeMQTransport supremeMQTransport) {
        if (supremeMQTransport == null) {
            throw new IllegalArgumentException("SugarMQTransport不能为空！");
        }
        //1.创建客户端传送器
        this.supremeMQTransport = supremeMQTransport;
        //2.消息分发器
    }

    @Override
    public Session createSession(boolean b, int i) throws JMSException {
        return null;
    }

    @Override
    public String getClientID() throws JMSException {
        return null;
    }

    @Override
    public void setClientID(String s) throws JMSException {

    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        return null;
    }

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        return null;
    }

    @Override
    public void setExceptionListener(ExceptionListener exceptionListener) throws JMSException {

    }

    @Override
    public void start() throws JMSException {

    }

    @Override
    public void stop() throws JMSException {

    }

    @Override
    public void close() throws JMSException {

    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Destination destination, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
        return null;
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i) throws JMSException {
        return null;
    }
}
