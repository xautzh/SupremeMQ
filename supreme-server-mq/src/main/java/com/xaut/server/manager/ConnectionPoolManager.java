package com.xaut.server.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 连接池管理
 * 因为一个JMS提供者可支持的连接资源是有限的，所以需要管理。
 */

public class ConnectionPoolManager {
    //1.客户端链接成功 占用一个链接名额
    //2/客户端移除后 释放一个链接名额
    private Logger logger = LoggerFactory.getLogger(ConnectionPoolManager.class);

    public final int MAX_CONNECTION_NUM = 500;	// 提供者支持的最大连接数
    private AtomicInteger CURRENT_CONNECTION_NUM;
    private boolean SUCCESS_INIT = false;

//	private static Map<Connection, ConnectionStatus> connectionMap = new ConcurrentHashMap<Connection, ConnectionStatus>(
//			MAX_CONNECTION_NUM);

    private ConnectionPoolManager(){
    }

    public synchronized void init() {
        if(!SUCCESS_INIT) {
//			connectionMap.clear();,
            CURRENT_CONNECTION_NUM = new AtomicInteger(0);
        }

        logger.info("Connection连接池初始化成功！");
        SUCCESS_INIT = true;
    }

    /**
     * 客户端连接成功后，调用该方法来占用一个连接名额
     * 如果已经没有可用连接，返回false
     */
    public synchronized boolean useOneConnection() {
        if(CURRENT_CONNECTION_NUM.get() < MAX_CONNECTION_NUM) {
            CURRENT_CONNECTION_NUM.incrementAndGet();
            return true;
        } else {
            logger.debug("暂时没有空闲可用的Connection！");
            return false;
        }
    }

    /**
     * 客户端成功关闭后，调用该方法来释放一个连接名额
     */
    public void receiveOneConnection() {
        CURRENT_CONNECTION_NUM.decrementAndGet();
    }
}
