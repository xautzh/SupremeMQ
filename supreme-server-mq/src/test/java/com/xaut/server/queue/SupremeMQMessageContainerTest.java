package com.xaut.server.queue;

import com.xaut.client.message.bean.SupremeMQTextMessage;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;

import java.util.List;

import static org.junit.Assert.*;

public class SupremeMQMessageContainerTest {
    SupremeMQMessageContainer supremeMQMessageContainer
            = new SupremeMQMessageContainer("supreme", "TOPIC");


    @Test
    public void putMessage() throws JMSException {
        SupremeMQTextMessage message = new SupremeMQTextMessage();

        message.setText("hello message");
        supremeMQMessageContainer.putMessage(message);

//        SupremeMQTextMessage msg = (SupremeMQTextMessage)supremeMQMessageContainer.takeMessage();
        List<SupremeMQTextMessage> msgList = (List) supremeMQMessageContainer.takeMessage(30);
        for (SupremeMQTextMessage txt : msgList) {
            System.out.println(txt.getText());
        }
    }

    @Test
    public void takeMessage() {
        Message msg = supremeMQMessageContainer.takeMessage();
        System.out.println(msg);
    }

    @Test
    public void takeMessage1() {
    }

    @Test
    public void remove() {
    }
}