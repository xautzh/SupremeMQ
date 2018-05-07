package com.xaut.server.transport;

import com.xaut.common.message.bean.SupremeMQMessage;
import com.xaut.server.manager.SupremeMQConsumerManager;
import com.xaut.server.manager.SupremeMQMessageManager;

import javax.jms.JMSException;

public interface SupremeMQTransprotCenter {
    void start() throws JMSException;

    void close();

    void remove(SupremeMQServerTransport supremeMQServerTransport);

    void setSupremeConsumerManager(SupremeMQConsumerManager supremeConsumerManager);

    void setSupremeMessageManager(SupremeMQMessageManager supremeMessageManager);
}
