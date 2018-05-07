package com.xaut.server.transport.tcp;


import com.xaut.server.dispatch.SupremeMQDestinationDispatcher;
import com.xaut.server.manager.SupremeMQConsumerManager;
import com.xaut.server.manager.SupremeMQMessageManager;
import com.xaut.server.transport.SupremeMQServerTransport;
import com.xaut.server.transport.SupremeMQTransprotCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.jms.JMSException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TcpSupremeMQTransportCenter implements SupremeMQTransprotCenter {

    private InetAddress inetAddress;
    private int port;
    private ServerSocket serverSocket;

    private @Value("${transport-backlog}")
    int backlog;

    private SupremeMQConsumerManager supremeMQConsumerManager;

    private SupremeMQMessageManager supremeMQMessageManager;

    private ConcurrentHashMap<TcpSupremeMQServerTransport, SupremeMQDestinationDispatcher>
            transportMap = new ConcurrentHashMap<>();


    private Logger logger = LoggerFactory.getLogger(TcpSupremeMQTransportCenter.class);

    public TcpSupremeMQTransportCenter(InetAddress inetAddress, int port) {
        if (inetAddress == null) {
            logger.error("InetAddress为空，创建TcpSugarMQTransprotCenter失败！");
            throw new IllegalArgumentException("InetAddress不能为空！");
        }
        this.inetAddress = inetAddress;
        this.port = port;
    }

    @Override
    public void start() throws JMSException {
        try {
            serverSocket = new ServerSocket(port, backlog, inetAddress);
        } catch (IOException e) {
            logger.error("ServerSocket初始化失败", e);
            throw new JMSException(String.format("TcpSupremeMQServerTransport绑定URI出错：【%s】【%s】【%s】",
                    new Object[]{inetAddress, port, e.getMessage()}));
        }
        TcpSupremeMQServerTransport tcpSupremeMQServerTransport = null;
        //Dispatcher
        SupremeMQDestinationDispatcher destinationDispatcher = null;
        Socket socket = null;
        while (true) {
            try {
                socket = serverSocket.accept();
                tcpSupremeMQServerTransport = new TcpSupremeMQServerTransport(socket);
                logger.debug("新的客户端【{}】",socket);
                //Dispatcher
                destinationDispatcher = new SupremeMQDestinationDispatcher(
                        tcpSupremeMQServerTransport.getReceiveMessageQueue(),
                        tcpSupremeMQServerTransport.getSendMessageQueue(),
                        supremeMQMessageManager,
                        supremeMQConsumerManager);
                tcpSupremeMQServerTransport.start();
                //Dispatcher.start()
                destinationDispatcher.start();
                //map.put(tcpSupremeMQServerTransport,Dispatcher)
                transportMap.put(tcpSupremeMQServerTransport, destinationDispatcher);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void close() throws JMSException {
        logger.info("TcpSugarMQTransprotCenter正在关闭... ...");
        for (Map.Entry<TcpSupremeMQServerTransport, SupremeMQDestinationDispatcher> entry : transportMap.entrySet()) {
            entry.getKey().close();
            entry.getValue().stop();
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("关闭ServerSocket异常", e);
            throw new JMSException(String.format("关闭ServerSocket异常:{}", e.getMessage()));
        }

    }

    @Override
    public void remove(SupremeMQServerTransport supremeMQServerTransport) {
        supremeMQServerTransport.close();
    }

    @Override
    public void setSupremeConsumerManager(SupremeMQConsumerManager supremeConsumerManager) {
        this.supremeMQConsumerManager = supremeConsumerManager;
    }

    @Override
    public void setSupremeMessageManager(SupremeMQMessageManager supremeMessageManager) {
        this.supremeMQMessageManager = supremeMessageManager;
    }

    public ConcurrentHashMap<TcpSupremeMQServerTransport, SupremeMQDestinationDispatcher> getTransportMap() {
        return transportMap;
    }

    public void setTransportMap(ConcurrentHashMap<TcpSupremeMQServerTransport, SupremeMQDestinationDispatcher> transportMap) {
        this.transportMap = transportMap;
    }
}
