package com.xaut.server.queue;


import com.xaut.client.message.SupremeMQDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 队列和主题的消息容器
 */
public class SupremeMQMessageContainer extends SupremeMQDestination {

    private static final long serialVersionUID = 2122365866558582491L;

    /**
     * 待发送的消息队列
     */
    private transient BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    /**
     * 已发送的消息队列
     */
    private transient BlockingQueue<Message> consumeMessageQueue = new LinkedBlockingQueue<>();

    private static Logger logger = LoggerFactory.getLogger(SupremeMQMessageContainer.class);

    public SupremeMQMessageContainer(String name, String type) {
        super(name, type);
    }

    /**
     * 往消息队列中放一条消息
     *
     * @param message
     * @throws JMSException
     */

    public void putMessage(Message message) throws JMSException {
        try {
            message.setJMSTimestamp(new Date().getTime());
            messageQueue.put(message);
            logger.debug("往队列【{}】添加一条消息:{}", name, message);
        } catch (InterruptedException e) {
            logger.error("往队列【{}】添加消息【{}】失败:{}", name, message, e);
            throw new JMSException(e.getMessage());
        }
    }

    /**
     * 从队列中获取消息
     * 没消息则阻塞
     *
     * @return
     */
    public Message takeMessage() {
        Message message = null;
        try {
            message = messageQueue.take();
            logger.debug("从队列【{}】中取出一条消息:{}", name, message);
        } catch (InterruptedException e) {
            logger.error("从队列【{}】中获取消息失败:{}", name, e);
            e.printStackTrace();
        }
        return message;
    }

    /**
     * 从队列中获取最多指定数量消息
     * 没消息则阻塞，不保证能获取到指定数量的消息，但能保证至少返回一条消息
     *
     * @param messageSize
     * @return
     * @throws JMSException
     */

    public List<Message> takeMessage(int messageSize) throws JMSException {
        if (messageSize == 0) {
            throw new IllegalArgumentException("指定消息数量必须大于0！");
        }
        List<Message> messageList = new ArrayList<>(messageSize);
        try {
            //保证至少获得一条消息
            messageList.add(messageQueue.take());
            //尝试获取剩下的消息
            Message msg = null;
            for (int i = 1; i < messageSize; i++) {
                msg = messageQueue.poll();
                if (msg != null) {
                    messageList.add(msg);
                }
            }
            logger.debug("从队列【{}】尝试取出【{}】条消息:【{}】", name, messageSize, messageList);

        } catch (InterruptedException e) {
            logger.error("从队列【{}】获取消息失败:{}", name, e);
            throw new JMSException(e.getMessage());
        }
        return messageList;
    }

    /**
     * 删除一条消息
     *
     * @param message
     */
    public void remove(Message message) {
        consumeMessageQueue.remove(message);

    }
}
