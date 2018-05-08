package com.xaut.server.transportServer;

import com.xaut.server.transport.tcp.TcpSupremeMQServerTransport;

import javax.jms.JMSException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TransportServer {
    private  static ServerSocket serverSocket;
    static {
        try {
            serverSocket = new ServerSocket(9090);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        while (true){
            try {
                Socket socket = serverSocket.accept();
                TcpSupremeMQServerTransport tcpSupremeMQServerTransport
                        = new TcpSupremeMQServerTransport(socket);
                tcpSupremeMQServerTransport.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
