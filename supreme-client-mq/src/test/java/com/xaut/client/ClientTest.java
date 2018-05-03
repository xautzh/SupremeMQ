package com.xaut.client;

import com.xaut.client.core.SupremeMQConnectionFactory;
import com.xaut.server.constant.MessageContainerType;
import com.xaut.server.message.SupremeMQDestination;
import org.junit.Test;

import javax.jms.*;

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
        consumer.setMessageListener(new MessageListener(){
            @Override
            public void onMessage(javax.jms.Message message) {
                try {
                    System.out.println("consumer+"+((TextMessage)message).getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
