package com.xaut.client.message.bean;

import com.xaut.client.message.Message;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.Serializable;

public class SupremeMQObjectMessage extends Message implements ObjectMessage {


    public SupremeMQObjectMessage() {
        super();
    }

    @Override
    public void setObject(Serializable serializable) throws JMSException {

    }

    @Override
    public Serializable getObject() throws JMSException {
        return null;
    }
}
