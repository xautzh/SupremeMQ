package com.xaut.server.queue;


import com.xaut.server.StudentTest;
import com.xaut.server.message.bean.SupremeMQObjectMessage;
import com.xaut.server.message.bean.SupremeMQTextMessage;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SupremeMQMessageContainerTest {
    Logger logger = LoggerFactory.getLogger(SupremeMQMessageContainerTest.class);
    SupremeMQMessageContainer supremeMQMessageContainer
            = new SupremeMQMessageContainer("supreme", "TOPIC");


    @Test
    public void putAndGetTextMessage() throws JMSException {
        SupremeMQTextMessage message = new SupremeMQTextMessage();
        for (int i = 0; i < 10; i++) {
            logger.debug("添加的当前消息id为：【{}】", i);
            message.setText("hello message" + i);
            supremeMQMessageContainer.putMessage(message);
        }
//        SupremeMQTextMessage msg = (SupremeMQTextMessage)supremeMQMessageContainer.takeMessage();
//        List<SupremeMQTextMessage> msgList = (List) supremeMQMessageContainer.takeMessage(30);
//        for (SupremeMQTextMessage txt : msgList) {
//            logger.debug("取出的消息为：【{}】", txt.getText());
//        }
        for (int i= 0;i<10;i++){
            supremeMQMessageContainer.takeMessage();
        }
    }

    @Test
    public void putAndGetObjectMessage() throws JMSException {
        SupremeMQObjectMessage message = new SupremeMQObjectMessage();
        for (int i = 0; i < 10; i++) {
            StudentTest studentTest = new StudentTest();
            studentTest.setId(i);
            logger.debug("添加的当前消息id为：【{}】", i);
            logger.debug("当前对象为：【{}】",studentTest);
            message.setObject(studentTest);
            supremeMQMessageContainer.putMessage(message);
        }
        List<SupremeMQObjectMessage> msgObjectList = (List) supremeMQMessageContainer.takeMessage(10);
        for (SupremeMQObjectMessage o : msgObjectList) {
            logger.debug("对象为：【{}】", o.getObject());
        }
    }

    @Test
    public void takeMessage() {
        Message msg = supremeMQMessageContainer.takeMessage();
        System.out.println(msg);
    }

    @Test
    public void takeMessage1() throws InterruptedException {
        BlockingQueue<StudentTest> blockingQueue = new LinkedBlockingQueue();
        for (int i = 0; i < 10; i++) {
            StudentTest studentTest = new StudentTest();
            studentTest.setId(i);
            blockingQueue.put(studentTest);
        }

        for (int i = 0; i < 10; i++) {
//            System.out.println("队列大小"+blockingQueue.size());
            System.out.println(blockingQueue.take());
        }
    }

    @Test
    public void remove() {
        System.out.println();
    }
}