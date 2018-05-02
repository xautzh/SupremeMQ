package com.xaut.server.manager;

import com.xaut.server.transport.SupremeMQTransprotCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

/**
 * server端核心管理类
 */
@Component
public class SupremeMQServerManager {

    private SupremeMQTransprotCenter supremeMQTransprotCenter;

    private Logger logger = LoggerFactory.getLogger(SupremeMQMessageManager.class);

    public void start() {
        new Thread(() -> {
            try {
                supremeMQTransprotCenter.start();
            } catch (JMSException e) {
                logger.error(e.getMessage());
            }
        }).start();
    }
}
