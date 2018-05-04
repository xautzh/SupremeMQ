package com.xaut.server.manager;





import com.xaut.common.message.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消费者管理器
 */
public class SupremeMQConsumerManager {
    //key-消费者id value-SupremeMQServerTransport的sendMessageQueue
    private ConcurrentHashMap<String,BlockingQueue<Message>> consumer = new ConcurrentHashMap<>();

    //1.注册消费者
    //2.将消息推送到一个消费者的待发送队列
    //3/生成消费者id  可以使用Md5加密算法 或者random随机  推荐前者 唯一性较高
}
