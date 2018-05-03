package com.xaut.server.transport.tcp;


import com.xaut.server.manager.SupremeMQConsumerManager;
import com.xaut.server.manager.SupremeMQMessageManager;
import com.xaut.server.message.bean.SupremeMQMessage;
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

public class TcpSupremeMQTransportCenter implements SupremeMQTransprotCenter {

    private InetAddress inetAddress;
    private int port;
    private ServerSocket serverSocket;

    private @Value("${transport-backlog}") int backlog;

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
    public void start() throws JMSException {
        try {
            serverSocket = new ServerSocket(port,backlog,inetAddress);
        } catch (IOException e) {
            logger.error("ServerSocket初始化失败", e);
            throw new JMSException(String.format("TcpSupremeMQServerTransport绑定URI出错：【%s】【%s】【%s】",
                    new Object[]{inetAddress, port, e.getMessage()}));
        }
        TcpSupremeMQServerTransport tcpSupremeMQServerTransport = null;
        //Dispatcher
        Socket socket = null;
        while (true){
            try {
                socket = serverSocket.accept();
                tcpSupremeMQServerTransport = new TcpSupremeMQServerTransport(socket,this);
                //Dispatcher
                tcpSupremeMQServerTransport.start();
                //Dispatcher.start()
                //map.put(tcpSupremeMQServerTransport,Dispatcher)
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


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
