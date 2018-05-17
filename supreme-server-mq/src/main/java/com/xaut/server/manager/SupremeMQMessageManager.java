package com.xaut.server.manager;

import com.xaut.common.constant.MessageContainerType;
import com.xaut.common.constant.MessageProperty;
import com.xaut.common.message.Message;
import com.xaut.common.message.SupremeMQDestination;
import com.xaut.common.message.bean.SupremeMQMessage;
import com.xaut.common.utils.MessageIdUtils;
import com.xaut.server.queue.SupremeMQMessageContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MOM队列管理 消息管理核心类
 */
@Component
public class SupremeMQMessageManager {
    // 队列中所能容纳的消息最大数
    private @Value("${max_queue_message_num}")
    int MAX_QUEUE_MESSAGE_CAPACITY;
    // 队列数量的最大值
    private @Value("${max_queue_num}")
    int MAX_QUEUE_NUM;
    //消息队列 key消息容器名称-消息容器中的name value-消息容器
    private ConcurrentHashMap<String, SupremeMQMessageContainer> messageContainerMap
            = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(SupremeMQMessageManager.class);

    /**
     * 将消息放入消息容器
     *
     * @param message
     * @throws JMSException
     */
    public void addMessage(Message message) throws JMSException {
        if (DeliveryMode.PERSISTENT == message.getJMSDeliveryMode()) {
            logger.debug("消息持久化：{}", message);
            //持久化接口
        }
        Destination destination = message.getJMSDestination();
        if (destination instanceof Queue) {
            logger.debug("队列消息【{}】", message);
            String name = ((Queue) destination).getQueueName();
            SupremeMQMessageContainer queue = getSupremeMQMessageContainer(name);
            if (messageContainerMap.size() >= MAX_QUEUE_NUM) {
                logger.warn("队列已满，添加失败：【{}】", name);
                throw new JMSException("队列已满，添加失败：【{}】", name);
            }
            message.setJMSDestination(queue);
            logger.debug("将消息放入分发队列", message);
            queue.putMessage(message);
        } else if (destination instanceof Topic) {
            logger.debug("主题消息【{}】", message);
        }
    }


    /**
     * 删除消息
     *
     * @param message
     * @throws JMSException
     */
    public void removeMessage(Message message) throws JMSException {
        if (DeliveryMode.PERSISTENT == message.getJMSDeliveryMode()) {
            logger.debug("消息持久化：{}", message);
            //持久化接口
        }
        SupremeMQDestination supremeMQDestination = (SupremeMQDestination) message.getJMSDestination();
        SupremeMQMessageContainer supremeMQMessageContainer = messageContainerMap.get(supremeMQDestination.getQueueName());
        if (supremeMQMessageContainer != null) {
            supremeMQMessageContainer.remove(message);
        } else {
            logger.warn("不存在的消息队列名称【{}】，删除消息失败【{}】", supremeMQDestination.getName(), message);
        }
    }

    /**
     * 生产者应答消息
     *
     * @param message
     * @return
     * @throws JMSException
     */
    public Message receiveProducerMessage(Message message) throws JMSException {
        //1.获取客户端给消息设置的消息id
        String clientSessionId = message.getJMSMessageID();
        if (!message.getBooleanProperty(MessageProperty.DISABLE_MESSAGE_ID.getKey())) {
            message.setJMSMessageID(MessageIdUtils.getNewMessageId());
        } else {
            message.setJMSMessageID(null);
        }
        addMessage(message);

        Message acknowledgeMessage = new SupremeMQMessage();
        acknowledgeMessage.setJMSMessageID(clientSessionId);
        return acknowledgeMessage;
    }

    public void receiveConsumerAcknowledgeMessage(Message message) throws JMSException {
        removeMessage(message);
    }

    /**
     * 构造消息容器
     *
     * @param name
     * @return
     */
    public SupremeMQMessageContainer getSupremeMQMessageContainer(String name) {
        SupremeMQMessageContainer queue = messageContainerMap.putIfAbsent(name,
                new SupremeMQMessageContainer(name, MessageContainerType.QUEUE.getValue()));
        if (queue == null) {
            queue = messageContainerMap.get(name);
        }
        return queue;
    }

//    public Destination getSupremeMQMessageContainer(String name) {
//        SupremeMQMessageContainer queue = messageContainerMap.putIfAbsent(name, new SupremeMQMessageContainer(name,
//                MessageContainerType.QUEUE.getValue()));
//
//        if(queue == null) {
//            queue = messageContainerMap.get(name);
//        }
//
//        return queue;
//    }
}
