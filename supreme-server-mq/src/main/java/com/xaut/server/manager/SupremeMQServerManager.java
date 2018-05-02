package com.xaut.server.manager;

import com.xaut.server.transport.SupremeMQServerTransport;
import com.xaut.server.transport.SupremeMQServerTransportFactory;
import com.xaut.server.transport.SupremeMQTransprotCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

/**
 * server端核心管理类
 */
@Component
public class SupremeMQServerManager {

    private @Value("${server_uri}") String url;

    @Autowired
    private SupremeMQTransprotCenter supremeMQTransprotCenter;

    private Logger logger = LoggerFactory.getLogger(SupremeMQMessageManager.class);

    public void start() throws JMSException {
        supremeMQTransprotCenter = SupremeMQServerTransportFactory.createSupremeMQTransport(url);
        new Thread(() -> {
            try {
                supremeMQTransprotCenter.start();
            } catch (JMSException e) {
                logger.error(e.getMessage());
            }
        }).start();
    }

    public String getUrl() {
        return url;
    }
}
