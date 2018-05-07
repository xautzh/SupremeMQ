package com.xaut.server.transport;

import com.xaut.server.manager.SupremeMQConsumerManager;
import com.xaut.server.manager.SupremeMQMessageManager;

import javax.jms.JMSException;

public interface SupremeMQTransprotCenter {
    void start() throws JMSException;

    void close() throws JMSException;

    void remove(SupremeMQServerTransport supremeMQServerTransport);

    void setSupremeConsumerManager(SupremeMQConsumerManager supremeConsumerManager);

    void setSupremeMessageManager(SupremeMQMessageManager supremeMessageManager);
}
