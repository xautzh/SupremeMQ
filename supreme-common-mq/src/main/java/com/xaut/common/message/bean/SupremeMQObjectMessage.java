package com.xaut.common.message.bean;



import com.xaut.common.message.Message;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.Serializable;

public class SupremeMQObjectMessage extends Message implements ObjectMessage {

    private static final long serialVersionUID = -3848421894529662497L;

    private Object object;


    public SupremeMQObjectMessage() {
        super();
    }

    @Override
    public void setObject(Serializable serializable) throws JMSException {
        this.object = serializable;
    }

    @Override
    public Serializable getObject() throws JMSException {
        return (Serializable) this.object;
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        return new byte[0];
    }

    @Override
    public void setJMSCorrelationIDAsBytes(byte[] bytes) throws JMSException {

    }

    @Override
    public void clearProperties() throws JMSException {

    }

    @Override
    public void acknowledge() throws JMSException {

    }

    @Override
    public void clearBody() throws JMSException {

    }
}
