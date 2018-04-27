package com.xaut.client.message;


import com.xaut.client.constant.MessageContainerType;
import org.apache.commons.lang3.StringUtils;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import java.io.Serializable;

public class SupremeMQDestination implements Queue, Topic, Serializable {
    private static final long serialVersionUID = 4315929928684782158L;
    /**
     * 消息名字
     */
    protected String name;
    /**
     * 消息类型
     */
    protected String type;

    public SupremeMQDestination(String name, String type) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(type)) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.type = type;
    }

    @Override
    public String getQueueName() throws JMSException {
        return name;
    }

    @Override
    public String getTopicName() throws JMSException {
        return name;
    }

    public boolean isQueue() {
        return MessageContainerType.QUEUE.getValue().equals(type);
    }

    public boolean isTopic() {
        return MessageContainerType.TOPIC.getValue().equals(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
