package com.xaut.server.manager;

import com.xaut.common.message.Message;
import com.xaut.common.message.bean.SupremeMQTextMessage;
import org.junit.Test;

import javax.jms.JMSException;

import static org.junit.Assert.*;

public class SupremeMQMessageManagerTest {
    SupremeMQMessageManager mqMessageManager = new SupremeMQMessageManager();

    @Test
    public void receiveProducerMessage() throws JMSException {
        SupremeMQTextMessage message = new SupremeMQTextMessage();
        message.setText("hello");
        System.out.println(mqMessageManager.receiveProducerMessage(message));
    }
}