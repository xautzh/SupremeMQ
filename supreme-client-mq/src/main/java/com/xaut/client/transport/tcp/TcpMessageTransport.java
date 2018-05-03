package com.xaut.client.transport.tcp;

import com.xaut.client.transport.SupremeMQTransport;
import com.xaut.server.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 采用socket来传输对象
 */

public class TcpMessageTransport extends SupremeMQTransport {
    private InetAddress inetAddress;
    private int port;
    private Socket socket;
    //收消息的队列
    private BlockingQueue<Message> receiveMessageQueue = new LinkedBlockingQueue<>();
    //发消息的队列
    private BlockingQueue<Message> sendMessageQueue = new LinkedBlockingQueue<>();
    //收消息线程
    private Thread receiveMessageThread;
    //发消息线程
    private Thread sendMessageThread;

    private byte[] objectByte = new byte[Message.OBJECT_BYTE_SIZE];

    private Logger logger = LoggerFactory.getLogger(TcpMessageTransport.class);

    public TcpMessageTransport(InetAddress inetAddress, int port) {
        if (inetAddress == null) {
            throw new IllegalArgumentException("InetAddress不能为空！");
        }
        this.inetAddress = inetAddress;
        this.port = port;
    }

    @Override
    public void start() throws JMSException {
        try {
            socket = new Socket(inetAddress, port);
            /**
             * 判断socket是否连接
             */
            if (!socket.isConnected()) {
                logger.error("Socket未连接,TcpMessageTransport启动失败");
                throw new JMSException("Socket未连接,TcpMessageTransport启动失败");
            }
            /**
             * 消息接收线程
             */
            if (!socket.isInputShutdown()) {
                receiveMessageThread = new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                logger.debug("TcpMessageTransport消息接收线程启动【{}】", this);
                                receiveMessage();
                            }
                        });
            } else {
                logger.debug("Socket未连接，TcpMessageTransport开启消息接收线程失败！");
            }

            /**
             * 消息发送线程
             */

            if (!socket.isOutputShutdown()) {
                sendMessageThread = new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                sendMessage();
                                logger.debug("TcpMessageTransport消息接收线程启动【{}】", this);
                            }
                        }
                );
            } else {
                logger.debug("Socket未连接，TcpMessageTransport开启消息发送线程失败！");
            }
        } catch (IOException e) {
            logger.error("Socket对象启动失败", e);
            throw new JMSException("TcpMessageTransport Socket对象启动失败:" + e.getMessage());
        }


    }

    @Override
    public void close() throws JMSException {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("关闭socket出错", e.getMessage());
                throw new JMSException("关闭socket出错", e.getMessage());
            }
        }

    }

    @Override
    public BlockingQueue<Message> getReceiveMessageQueue() {
        return receiveMessageQueue;
    }

    @Override
    public BlockingQueue<Message> getSendMessageQueue() {
        return sendMessageQueue;
    }

    /**
     * 向socket中发送消息
     */
    private void sendMessage() {
        Message message = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        while (true) {
            try {
                message = sendMessageQueue.take();
                logger.debug("即将发送消息【{}】", message);
            } catch (InterruptedException e) {
                logger.info("TcpMessageTransport消息发送线程被要求停止！");
                break;
            }
            if (!Thread.currentThread().isInterrupted()
                    && !socket.isClosed()
                    && !socket.isOutputShutdown()) {

                try {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                    objectOutputStream.writeObject(message);
                    objectOutputStream.flush();
                    logger.debug("消息发送完毕【{}】", message);
                } catch (IOException e) {
                    logger.error("消息【{}】发送失败失败：{}", message, e);
                } finally {
                    if (objectOutputStream != null) {
                        try {
                            objectOutputStream.close();
                        } catch (IOException e) {
                            logger.error("关闭objectOutputStream失败");
                            e.printStackTrace();
                        }
                    }

                    if (byteArrayOutputStream != null) {
                        try {
                            byteArrayOutputStream.close();
                        } catch (IOException e) {
                            logger.error("关闭byteArrayOutputStream失败");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 从socket中接受消息
     */

    private void receiveMessage() {
        try {
            Message message = null;
            ObjectInputStream objectInputStream = null;
            Object receiveMessageObject = null;
            while (!Thread.currentThread().isInterrupted()
                    && !socket.isClosed()
                    && !socket.isInputShutdown()) {
                int byteNum = socket.getInputStream().read(objectByte);
                if (byteNum <= 0) {
                    continue;
                }
                objectInputStream = new ObjectInputStream(new ByteArrayInputStream(objectByte, 0, byteNum));
                try {
                    receiveMessageObject = objectInputStream.readObject();
                    logger.debug("读取的消息为【{}】", receiveMessageObject);
                } catch (ClassNotFoundException e) {
                    logger.error("读取到的消息为空");
                }
                if (!(receiveMessageObject instanceof Message)) {
                    logger.warn("客户端收到一个非法消息", receiveMessageObject);
                    continue;
                }
                message = (Message) receiveMessageObject;
                logger.info("客户端收到一条消息【{}】", message);
                receiveMessageQueue.put(message);
            }
            logger.error("Socket状态异常，TcpMessageTransport接收消息线程结束！");
        } catch (Exception e) {
            logger.error("TcpMessageTransport消息接收线程错误", e);
        }
    }
}
