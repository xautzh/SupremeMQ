package com.xaut.server;

import com.xaut.server.manager.SupremeMQServerManager;
import com.xaut.server.transport.SupremeMQServerTransportFactory;
import com.xaut.server.transport.tcp.TcpSupremeMQTransportCenter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.jms.JMSException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerTest {
    @Test
    public void serverTest() throws JMSException, IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        SupremeMQServerManager supremeMQServerManager = (SupremeMQServerManager) context.getBean("supremeMQServerManager");
        System.out.println(supremeMQServerManager.getUrl());
        //启动消息中间件服务器
        supremeMQServerManager.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            String line = reader.readLine();
            if("exit".equals(line)) {
                break;
            }
        }
    }
    @Test
    public void server() throws JMSException {
        SupremeMQServerManager supremeMQServerManager = new SupremeMQServerManager();
        supremeMQServerManager.start();
    }
    @Test
    public void service() throws JMSException {

    }
}
