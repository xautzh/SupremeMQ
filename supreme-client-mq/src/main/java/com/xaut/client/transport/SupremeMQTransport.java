package com.xaut.client.transport;


import com.xaut.common.message.Message;

import javax.jms.JMSException;
import java.util.concurrent.BlockingQueue;

/**
 * 客户端传送器接口
 */

public abstract class SupremeMQTransport {
    /**
     * 消息应答类型
     */
    public int ackonowledgeType;

    public int getAckonowledgeType() {
        return ackonowledgeType;
    }

    public void setAckonowledgeType(int ackonowledgeType) {
        this.ackonowledgeType = ackonowledgeType;
    }

    /**
     * 开启传送通道
     */
    public abstract void start() throws JMSException;

    /**
     * 关闭传送通道
     */
    public abstract void close() throws JMSException;

    /**
     * 获取收到的消息队列
     *
     * @return
     */
    public abstract BlockingQueue<Message> getReceiveMessageQueue();

    /**
     * 获取要发送的消息队列
     *
     * @return
     */
    public abstract BlockingQueue<Message> getSendMessageQueue();
}
