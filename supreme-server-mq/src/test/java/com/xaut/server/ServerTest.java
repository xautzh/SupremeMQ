package com.xaut.server;

import com.xaut.server.manager.SupremeMQServerManager;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerTest {
    @Test
    public void serverTest(){
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        SupremeMQServerManager supremeMQServerManager = (SupremeMQServerManager) context.getBean("supremeMQServerManager");
        //启动消息中间件服务器
        supremeMQServerManager.start();
    }
}
