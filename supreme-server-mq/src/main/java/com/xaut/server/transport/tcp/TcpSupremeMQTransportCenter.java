package com.xaut.server.transport.tcp;

import com.xaut.client.message.bean.SupremeMQMessage;
import com.xaut.server.manager.SupremeMQConsumerManager;
import com.xaut.server.manager.SupremeMQMessageManager;
import com.xaut.server.transport.SupremeMQServerTransport;
import com.xaut.server.transport.SupremeMQTransprotCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.ServerSocket;

public class TcpSupremeMQTransportCenter implements SupremeMQTransprotCenter {

    private InetAddress inetAddress;
    private int port;
    private ServerSocket serverSocket;

    private SupremeMQConsumerManager supremeMQConsumerManager;
    private SupremeMQMessageManager supremeMQMessageManager;


    private Logger logger = LoggerFactory.getLogger(TcpSupremeMQTransportCenter.class);

    public TcpSupremeMQTransportCenter(InetAddress inetAddress, int port) {
        if(inetAddress == null) {
            logger.error("InetAddress为空，创建TcpSugarMQTransprotCenter失败！");
            throw new IllegalArgumentException("InetAddress不能为空！");
        }
        this.inetAddress = inetAddress;
        this.port = port;
    }

    @Override
    public void start() {

    }

    @Override
    public void close() {

    }

    @Override
    public void remove(SupremeMQServerTransport supremeMQServerTransport) {

    }

    @Override
    public void setSupremeConsumerManager(SupremeMQConsumerManager supremeConsumerManager) {

    }

    @Override
    public void seatSupremeMessageManager(SupremeMQMessage supremeMessageManager) {

    }
}
