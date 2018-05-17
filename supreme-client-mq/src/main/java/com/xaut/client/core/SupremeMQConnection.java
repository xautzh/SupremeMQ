package com.xaut.client.core;

import com.xaut.client.transport.MessageDispatcher;
import com.xaut.client.transport.SupremeMQTransport;
import com.xaut.common.constant.ConnectionProperty;
import com.xaut.common.constant.MessageType;
import com.xaut.common.message.bean.SupremeMQMapMessage;
import com.xaut.common.utils.SessionIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class SupremeMQConnection implements Connection {
    private SupremeMQTransport supremeMQTransport;

    private MessageDispatcher messageDispatcher;

    // 消费者消费消息和发送应答消息的线程池执行器
    private ThreadPoolExecutor threadPoolExecutor;

    // key-sessionID，value-session
    private Map<String, SupremeMQSession> sessionMap = new ConcurrentHashMap<String, SupremeMQSession>();

    private AtomicBoolean isStarted = new AtomicBoolean(false);
    private AtomicBoolean isClosed = new AtomicBoolean(false);

    // 连接参数Map
    private ConcurrentMap<String, Object> params = new ConcurrentHashMap<String, Object>();

    private Logger logger = LoggerFactory.getLogger(SupremeMQConnection.class);

    public SupremeMQConnection(SupremeMQTransport supremeMQTransport) {
        if (supremeMQTransport == null) {
            throw new IllegalArgumentException("SugarMQTransport不能为空！");
        }
        //1.创建客户端传送器
        this.supremeMQTransport = supremeMQTransport;
        //2.消息分发器
        logger.debug("我的调试，当前接收队列内容【{}】，当前发送队列内容【{}】",supremeMQTransport.getReceiveMessageQueue(),supremeMQTransport.getSendMessageQueue());
        this.messageDispatcher = new MessageDispatcher(supremeMQTransport.getReceiveMessageQueue(),
                supremeMQTransport.getSendMessageQueue());
    }

    @Override
    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        supremeMQTransport.setAckonowledgeType(acknowledgeMode);
        String sessionId = SessionIdUtils.getNewSessionId();
        SupremeMQSession supremeMQSession = new SupremeMQSession(sessionId, transacted, messageDispatcher);
        sessionMap.put(sessionId, supremeMQSession);
        return supremeMQSession;
    }

    @Override
    public String getClientID() throws JMSException {
        return null;
    }

    @Override
    public void setClientID(String s) throws JMSException {

    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        return null;
    }

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        return null;
    }

    @Override
    public void setExceptionListener(ExceptionListener exceptionListener) throws JMSException {

    }

    @Override
    public void start() throws JMSException {
        if (isStarted.get()) {
            return;
        }
        logger.info("SupremeMQConnection开始启动！");
        supremeMQTransport.start();
        messageDispatcher.start();
        //将客户端定制参数传递给服务端
//        SupremeMQMapMessage message = new SupremeMQMapMessage();
//        message.setJMSType(MessageType.CUSTOMER_MESSAGE_PULL.getValue());
//        message.setInt(ConnectionProperty.CLIENT_MESSAGE_BATCH_ACK_QUANTITY.getKey(),
//                (Integer) ConnectionProperty.CLIENT_MESSAGE_BATCH_ACK_QUANTITY.getValue());
//        messageDispatcher.sendMessage(message);
        // 消费者消费消息和发送应答消息的线程池执行器
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        for (SupremeMQSession supremeMQSession : sessionMap.values()) {
            supremeMQSession.start();
        }
        isStarted.set(true);
    }

    @Override
    public void stop() throws JMSException {
        supremeMQTransport.close();
    }

    @Override
    public void close() throws JMSException {

    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Destination destination, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
        return null;
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i) throws JMSException {
        return null;
    }
}
