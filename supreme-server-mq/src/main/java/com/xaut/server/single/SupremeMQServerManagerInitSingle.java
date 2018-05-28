package com.xaut.server.single;

import com.xaut.server.manager.SupremeMQServerManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SupremeMQServerManagerInitSingle {
    ApplicationContext context =
            new ClassPathXmlApplicationContext("applicationContext.xml");
    SupremeMQServerManager supremeMQServerManager =
            (SupremeMQServerManager) context.getBean("supremeMQServerManager");
    private static SupremeMQServerManagerInitSingle supremeMQServerManagerInitSingle;

    private SupremeMQServerManagerInitSingle() {
    }

    public static SupremeMQServerManagerInitSingle getSupremeMQServerManagerInitSingle() {
        if (supremeMQServerManagerInitSingle == null)
            supremeMQServerManagerInitSingle = new SupremeMQServerManagerInitSingle();
        return supremeMQServerManagerInitSingle;
    }

    public SupremeMQServerManager getSupremeMQServerManager() {
        return supremeMQServerManager;
    }
}
