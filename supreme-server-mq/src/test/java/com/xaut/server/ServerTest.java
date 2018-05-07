package com.xaut.server;

import com.xaut.server.manager.SupremeMQServerManager;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.jms.JMSException;

public class ServerTest {
    @Test
    public void serverTest() throws JMSException {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        SupremeMQServerManager supremeMQServerManager = (SupremeMQServerManager) context.getBean("supremeMQServerManager");
        System.out.println(supremeMQServerManager.getUrl());
        //启动消息中间件服务器
        supremeMQServerManager.start();
    }
    @Test
    public void server() throws JMSException {
        SupremeMQServerManager supremeMQServerManager = new SupremeMQServerManager();
        supremeMQServerManager.start();
    }
}
