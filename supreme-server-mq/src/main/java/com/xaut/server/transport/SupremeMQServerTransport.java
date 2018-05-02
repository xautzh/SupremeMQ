package com.xaut.server.transport;

import com.xaut.client.message.Message;

import java.util.concurrent.BlockingQueue;

public interface SupremeMQServerTransport {
    /**
     * 开启传送通道
     */
    void start();

    /**
     * 关闭传送通道
     */
    void close();

    /**
     * 获取发送的消息队列
     * @return
     */
    BlockingQueue<Message> getSendMessageQueue();

    /**
     * 获取接收的消息队列
     * @return
     */
    BlockingQueue<Message> getReceiveMessageQueue();

}
