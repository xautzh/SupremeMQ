package com.xaut.common.message.bean;



import com.xaut.common.message.Message;

import javax.jms.JMSException;
import javax.jms.TextMessage;

public class SupremeMQTextMessage extends Message implements TextMessage {

    private static final long serialVersionUID = 630846251491739491L;
    /**
     * 文本消息
     */
    private String textMessage;

    public SupremeMQTextMessage() {
        super();
    }

    public SupremeMQTextMessage(String textMessage) {
        super();
        this.textMessage = textMessage;
    }


    @Override
    public void setText(String s) throws JMSException {
        this.textMessage = s;

    }

    @Override
    public String getText() throws JMSException {
        return this.textMessage;
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
