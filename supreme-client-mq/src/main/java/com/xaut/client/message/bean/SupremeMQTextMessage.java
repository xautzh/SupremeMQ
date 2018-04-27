package com.xaut.client.message.bean;

import com.xaut.client.message.Message;

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
}
