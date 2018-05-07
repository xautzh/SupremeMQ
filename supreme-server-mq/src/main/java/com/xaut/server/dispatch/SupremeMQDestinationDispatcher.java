package com.xaut.server.dispatch;

import com.xaut.common.constant.ConnectionProperty;
import com.xaut.common.constant.MessageProperty;
import com.xaut.common.constant.MessageType;
import com.xaut.common.message.Message;
import com.xaut.common.message.SupremeMQDestination;
import com.xaut.common.message.bean.SupremeMQMapMessage;
import com.xaut.common.message.bean.SupremeMQMessage;
import com.xaut.server.manager.SupremeMQConsumerManager;
import com.xaut.server.manager.SupremeMQMessageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.util.concurrent.BlockingQueue;

/**
 * 消息目的地分发器
 * 每个Transprot连接器配置一个该分发器来分发消息到目的地
 */

public class SupremeMQDestinationDispatcher {
    //实现主要用于生产者-使用者队列，BlockingQueue 实现是线程安全的。
    // 所有排队方法都可以使用内部锁或其他形式的并发控制来自动达到它们的目的
    private BlockingQueue<Message> receiveMessageQueue;
    private BlockingQueue<Message> sendMessageQueue;
    private SupremeMQMessageManager supremeMQMessageManager;
    private SupremeMQConsumerManager supremeMQCustomerManager;
    private Thread dispatcherThread;

    private static Logger logger = LoggerFactory.getLogger(SupremeMQDestinationDispatcher.class);

    public SupremeMQDestinationDispatcher(BlockingQueue<Message> receiveMessageQueue, BlockingQueue<Message> sendMessageQueue,
                                          SupremeMQMessageManager supremeMQMessageManager, SupremeMQConsumerManager supremeMQCustomerManager) {
        if (receiveMessageQueue == null || sendMessageQueue == null || supremeMQMessageManager == null
                || supremeMQCustomerManager == null) {
            throw new IllegalArgumentException();
        }
        this.receiveMessageQueue = receiveMessageQueue;
        this.sendMessageQueue = sendMessageQueue;
        this.supremeMQMessageManager = supremeMQMessageManager;
        this.supremeMQCustomerManager = supremeMQCustomerManager;
    }

    public void start() {
        logger.info("SupremeMQDestinationDispatcher准备开始工作... ...");

        dispatcherThread = new Thread(() -> {
            Message message = null;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    message = receiveMessageQueue.take();
                } catch (InterruptedException e) {
                    logger.info("SupremeMQQueueDispatcher被中断，即将退出.");
                    break;
                }
                logger.debug("开始处理消息【{}】", message);
                //生产者消息
                try {
                    if (MessageType.PRODUCER_MESSAGE.getValue().equals(message.getJMSType())) {
                        logger.debug("生产者消息【{}】", message);
                        supremeMQMessageManager.addMessage(message);
                        //创建 应答消息
                        SupremeMQMessage answerMessage = new SupremeMQMessage();
                        answerMessage.setJMSType(MessageType.PRODUCER_ACKNOWLEDGE_MESSAGE.getValue());
                        answerMessage.setJMSMessageID(message.getJMSMessageID());
                        try {
                            sendMessageQueue.put(answerMessage);
                        } catch (InterruptedException e) {
                            logger.info("生产者应答消息发送被中断.");
                        }
                    }
                    //消费者 应答消息
                    else if (MessageType.CUSTOMER_ACKNOWLEDGE_MESSAGE.getValue().equals(message.getJMSType())) {
                        logger.debug("消费者应答消息【{}】", message);
                        supremeMQMessageManager.removeMessage(message);
                    }
                    //消费者 注册消息
                    else if (MessageType.CUSTOMER_REGISTER_MESSAGE.getValue().equals(message.getJMSType())) {
                        logger.debug("消费者注册消息【{}】", message);
                        SupremeMQDestination dest = (SupremeMQDestination) message.getJMSDestination();
                        message.setJMSDestination(supremeMQMessageManager.getSupremeMQMessageContainer(dest.getName()));
                        supremeMQCustomerManager.addConsumer(message, sendMessageQueue);
                    }
                    //消费者 拉取消息
                    else if (MessageType.CUSTOMER_MESSAGE_PULL.getValue().equals(message.getJMSType())) {
                        logger.debug("消费者拉取消息【{}】", message);
                        SupremeMQDestination dest = (SupremeMQDestination) message.getJMSDestination();
                        message.setJMSDestination(supremeMQMessageManager.getSupremeMQMessageContainer(dest.getName()));

                        supremeMQCustomerManager.updateConsumerState(dest.getName(),
                                message.getStringProperty(MessageProperty.CUSTOMER_ID.getKey()), true);
                    }
                    //链接初始化参数消息
                    else if (MessageType.CONNECTION_INIT_PARAM.getValue().equals(message.getJMSType())) {
                        logger.debug("连接初始化参数的消息【{}】", message);
                        SupremeMQMapMessage mapMessage = (SupremeMQMapMessage) message;
                        if (mapMessage.propertyExists(ConnectionProperty.CLIENT_MESSAGE_BATCH_ACK_QUANTITY.getKey())) {
                            supremeMQCustomerManager.setClientMessageBatchSendAmount(mapMessage.
                                    getInt(ConnectionProperty.CLIENT_MESSAGE_BATCH_ACK_QUANTITY.getKey()));
                        }
                    } else {
                        logger.error("未知消息类型，无法处理【{}】", message);
                    }
                } catch (JMSException e) {
                    logger.error("SupremeMQQueueDispatcher消息处理失败:【{}】", message, e);
                }
            }

        });

        dispatcherThread.start();
        logger.info("SugarMQDestinationDispatcher已经开始工作");
    }
    /**
     * 关闭
     */
    public void stop() {
        if(dispatcherThread != null) {
            dispatcherThread.interrupt();
        }
    }
}
