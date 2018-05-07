package com.xaut.server.dispatch;

import com.xaut.common.message.Message;
import com.xaut.server.manager.SupremeMQConsumerManager;
import com.xaut.server.queue.SupremeMQMessageContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消费者消息分发器
 * 每个消息队列配置一个
 */

public class SupremeMQConsumerDispatcher {
    private SupremeMQConsumerManager supremeMQCustomerManager;
    private SupremeMQMessageContainer supremeMQMessageContainer;
    private Thread dispatcherThread;
    private AtomicBoolean isStart = new AtomicBoolean(false);

    private static Logger logger = LoggerFactory.getLogger(SupremeMQConsumerDispatcher.class);

    public SupremeMQConsumerDispatcher(SupremeMQConsumerManager supremeMQConsumerManager, SupremeMQMessageContainer container) {
        if(supremeMQCustomerManager == null) {
            throw new IllegalArgumentException("SugarMQCustomerManager不能为空！");
        }

        if(supremeMQMessageContainer == null) {
            throw new IllegalArgumentException("SugarMQMessageContainer不能为空！");
        }
        this.supremeMQCustomerManager = supremeMQConsumerManager;
        this.supremeMQMessageContainer = container;
    }

    public void start(){
        logger.info("SupremeMQConsumerDispatcher准备开始工作... ...");
        dispatcherThread = new Thread(() -> {
            Message message = null;
            while(true){
                try {
                    message = supremeMQMessageContainer.takeMessage();
                    logger.debug("从SupremeMQMessageContainer中拉取了一条消息:【{}】", message);
                    //放入消费者队列
                    supremeMQCustomerManager.putMessageToCustomerQueue(message);
                } catch (Exception e) {
                    logger.info("SupremeMQCustomerDispatcher被中断！");
                    break ;
                }
            }
        });
    }

    public boolean isStart() {
        return isStart.get();
    }
}
