package com.xaut.client;

import com.xaut.client.core.SupremeMQConnectionFactory;
import com.xaut.common.constant.MessageContainerType;
import com.xaut.common.message.SupremeMQDestination;
import com.xaut.common.message.bean.SupremeMQTextMessage;
import org.junit.Test;

import javax.jms.*;
import java.io.BufferedReader;
import java.lang.management.BufferPoolMXBean;
import java.util.Scanner;

public class ClientTest {
    @Test
    public void clientTest() throws JMSException {
        //1.Factory 用于创建连接到消息中间件的连接工厂
        SupremeMQConnectionFactory factory = new SupremeMQConnectionFactory("tcp://127.0.0.1:1314");
        //1.Connection 代表了应用程序和消息服务器之间的通信链路
        Connection connection = factory.createConnection();
        //3.Destination 指消息发布和接收的地点，包括队列或主题
        Queue queue = new SupremeMQDestination("supreme", MessageContainerType.QUEUE.getValue());
        //4.Session 表示一个单线程的上下文，用于发送和接收消息
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //5. Message 是在消费者和生产者之间传送的对象， 消息头，一组消息属性，一个消息体
        TextMessage txtMessage = session.createTextMessage();
        txtMessage.setText("hello jms");
        //6. MessageProducer由会话创建，用于发送消息到目标
        MessageProducer producer = session.createProducer(queue);
        producer.send(txtMessage);
        //7. MessageConsumer由会话创建，用于接收发送到目标的消息
        MessageConsumer consumer = session.createConsumer(queue);
        //8.消息监听
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(javax.jms.Message message) {
                try {
                    System.out.println("consumer+" + ((TextMessage) message).getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Test
    public void client() throws JMSException {
        SupremeMQConnectionFactory factory = new SupremeMQConnectionFactory("tcp://127.0.0.1:9090");
        Connection connection = factory.createConnection();
        connection.start();
        Queue queue = new SupremeMQDestination("supreme", MessageContainerType.QUEUE.getValue());
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(queue);
        //String rcvMessage = ((TextMessage)consumer.receiveNoWait()).getText();
        //System.out.println("消费者收生产者发送的消息"+rcvMessage);
//        consumer.setMessageListener(message -> {
//            try {
//                System.out.println("consumer" + ((SupremeMQTextMessage) message).getText());
//            } catch (JMSException e) {
//                e.printStackTrace();
//            }
//        });
        MessageProducer producer = session.createProducer(queue);
        SupremeMQTextMessage textMessage = (SupremeMQTextMessage) session.createTextMessage();
        textMessage.setText("hello");
        producer.send(textMessage);
        System.out.println("consumer消费"+((TextMessage)consumer.receiveNoWait()).getText());
        while (true){
            Scanner scanner = new Scanner(System.in);
            Object o = scanner.hasNext();
            System.out.println(o);
        }
    }
}
