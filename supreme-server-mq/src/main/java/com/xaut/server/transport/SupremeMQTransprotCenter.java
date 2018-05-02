package com.xaut.server.transport;

import com.xaut.client.message.bean.SupremeMQMessage;
import com.xaut.server.manager.SupremeMQConsumerManager;

import javax.jms.JMSException;

public interface SupremeMQTransprotCenter {
    void start() throws JMSException;

    void close();

    void remove(SupremeMQServerTransport supremeMQServerTransport);

    void setSupremeConsumerManager(SupremeMQConsumerManager supremeConsumerManager);

    void seatSupremeMessageManager(SupremeMQMessage supremeMessageManager);
}
