package com.xaut.server.transport;

import com.xaut.client.message.bean.SupremeMQMessage;
import com.xaut.server.manager.SupremeMQConsumerManager;

public interface SupremeMQTransprotCenter {
    void start();

    void close();

    void remove(SupremeMQServerTransport supremeMQServerTransport);

    void setSupremeConsumerManager(SupremeMQConsumerManager supremeConsumerManager);

    void seatSupremeMessageManager(SupremeMQMessage supremeMessageManager);
}
