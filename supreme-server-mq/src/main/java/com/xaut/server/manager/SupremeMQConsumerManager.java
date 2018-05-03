package com.xaut.server.manager;



import com.xaut.server.message.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消费者管理器
 */
public class SupremeMQConsumerManager {
    //key-消费者id value-SupremeMQServerTransport的sendMessageQueue
    private ConcurrentHashMap<String,BlockingQueue<Message>> consumer = new ConcurrentHashMap<>();
}
