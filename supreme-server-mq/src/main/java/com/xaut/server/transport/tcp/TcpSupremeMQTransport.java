package com.xaut.server.transport.tcp;

import com.xaut.client.message.Message;
import com.xaut.server.transport.SupremeMQServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TcpSupremeMQTransport implements SupremeMQServerTransport {
    private Socket socket;
    //接收消息队列
    private BlockingQueue<Message> sendMessageQueue = new LinkedBlockingQueue<>();
    //发送消息的队列
    private BlockingQueue<Message> receiveMessageQueue = new LinkedBlockingQueue<>();
    //接收消息的线程
    private Thread sendMessageThread;
    //发送消息的线程
    private Thread receiveMessageThread;

    private AtomicBoolean isClosed = new AtomicBoolean(false);

    private byte[] objectByte = new byte[Message.OBJECT_BYTE_SIZE];

    private Logger logger = LoggerFactory.getLogger(TcpSupremeMQTransport.class);


    @Override
    public void start() {

    }

    @Override
    public void close() {

    }

    @Override
    public BlockingQueue<Message> getSendMessageQueue() {
        return null;
    }

    @Override
    public BlockingQueue<Message> getReceiveMessageQueue() {
        return null;
    }
}
