package com.xaut.server.queue;

import com.xaut.client.message.bean.SupremeMQTextMessage;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;

import java.util.List;

import static org.junit.Assert.*;

public class SupremeMQMessageContainerTest {
    Logger logger = LoggerFactory.getLogger(SupremeMQMessageContainerTest.class);
    SupremeMQMessageContainer supremeMQMessageContainer
            = new SupremeMQMessageContainer("supreme", "TOPIC");


    @Test
    public void putMessage() throws JMSException {
        SupremeMQTextMessage message = new SupremeMQTextMessage();
        for (int i = 0; i < 10; i++) {
            logger.debug("添加的当前消息id为：【{}】", i);
            message.setText("hello message" + i);
            supremeMQMessageContainer.putMessage(message);
        }

//        SupremeMQTextMessage msg = (SupremeMQTextMessage)supremeMQMessageContainer.takeMessage();
        List<SupremeMQTextMessage> msgList = (List) supremeMQMessageContainer.takeMessage(30);
        for (SupremeMQTextMessage txt : msgList) {
            logger.debug("取出的消息为：【{}】",txt.getText());
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