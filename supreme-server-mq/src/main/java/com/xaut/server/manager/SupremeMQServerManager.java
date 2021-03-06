package com.xaut.server.manager;

import com.xaut.server.transport.SupremeMQServerTransportFactory;
import com.xaut.server.transport.SupremeMQTransprotCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * server端核心管理类
 */
@Component
@PropertySource(value = "classpath:supremeMq-config.properties",ignoreResourceNotFound = true)
public class SupremeMQServerManager {

    private @Value("${server_uri}")
    String url;

    private AtomicBoolean START_STATUS = new AtomicBoolean();

    private SupremeMQTransprotCenter supremeMQTransprotCenter;
    @Autowired
    private SupremeMQConsumerManager supremeMQConsumerManager;
    @Autowired
    private SupremeMQMessageManager supremeMQMessageManager;
    @Autowired
    private ConnectionPoolManager connectionPoolManager;
    @Autowired
    SupremeMQServerTransportFactory supremeMQServerTransportFactory;


    private Logger logger = LoggerFactory.getLogger(SupremeMQMessageManager.class);

    public void start() throws JMSException {
        if (START_STATUS.get()) {
            logger.info("Supreme已经成功启动！");
            return;
        }
        //连接池初始化
        connectionPoolManager.init();
        //根据url创建supremeCenter
        supremeMQTransprotCenter =
                SupremeMQServerTransportFactory.createSupremeMQTransport(url);
        supremeMQTransprotCenter.setSupremeMessageManager(supremeMQMessageManager);
        supremeMQTransprotCenter.setSupremeConsumerManager(supremeMQConsumerManager);
        new Thread(() -> {
            try {
                supremeMQTransprotCenter.start();
            } catch (JMSException e) {
                logger.error(e.getMessage());
            }
        }).start();
        START_STATUS.set(true);
        logger.info("Supreme启动完毕:{}", url);
    }

    public String getUrl() {
        return url;
    }

    public SupremeMQConsumerManager getSupremeMQConsumerManager() {
        return supremeMQConsumerManager;
    }

    public SupremeMQMessageManager getSupremeMQMessageManager() {
        return supremeMQMessageManager;
    }
}
