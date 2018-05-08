package com.xaut.client.transportClient;

import com.xaut.client.core.SupremeMQConnectionFactory;
import com.xaut.client.transport.SupremeMQTransport;
import com.xaut.client.transport.SupremeMQTransportFactory;
import com.xaut.client.transport.tcp.TcpMessageTransport;

import javax.jms.JMSException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TransportClient {
    public static void main(String[] args) {
        try {

            TcpMessageTransport tcpMessageTransport = new TcpMessageTransport(InetAddress.getLocalHost(), 9090);
            tcpMessageTransport.start();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
