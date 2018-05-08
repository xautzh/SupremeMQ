package com.xaut.server.transport.tcp;


import com.xaut.common.message.Message;
import com.xaut.server.transport.SupremeMQServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TcpSupremeMQServerTransport implements SupremeMQServerTransport {

    private Socket socket;

//    private final TcpSupremeMQTransportCenter tcpSupremeMQTransportCenter;
    //发送消息的队列
    private BlockingQueue<Message> sendMessageQueue = new LinkedBlockingQueue<>();
    //接收消息队列
    private BlockingQueue<Message> receiveMessageQueue = new LinkedBlockingQueue<>();
    //接收消息的线程
    private Thread sendMessageThread;
    //发送消息的线程
    private Thread receiveMessageThread;

    private AtomicBoolean isClosed = new AtomicBoolean(false);

//    private byte[] objectByte = new byte[Message.OBJECT_BYTE_SIZE];

    private Logger logger = LoggerFactory.getLogger(TcpSupremeMQServerTransport.class);

    public TcpSupremeMQServerTransport(Socket socket) {
        if (socket == null) {
            logger.error("Socket对象不能为空！");
            throw new IllegalArgumentException("Socket不能为空！");
        }

        this.socket = socket;
//        this.tcpSupremeMQTransportCenter = tcpSupremeMQTransportCenter;
    }

    /**
     * 开启传送通道
     *
     * @throws JMSException
     */
    @Override
    public void start() throws JMSException {
        if (socket.isClosed()) {
            logger.error("Socket已经关闭，TcpSugarMQServerTransport启动失败！");
            throw new JMSException("Socket已经关闭，TcpSugarMQServerTransport启动失败！");
        }
        if (!socket.isConnected()) {
            logger.error("Socket未连接，TcpSugarMQServerTransport启动失败！");
            throw new JMSException("Socket未连接，TcpSugarMQServerTransport启动失败！");
        }
        //消息接收线程
        if (!socket.isInputShutdown()) {
            receiveMessageThread = new Thread(this::receiveMessage);
            logger.debug("服务端消息接收线程启动");
            receiveMessageThread.start();
        } else {
            logger.debug("Socket未连接，TcpSupremeMQServerTransport开启消息接收线程失败！");
        }
        //消息发送线程
        if (!socket.isOutputShutdown()) {
            sendMessageThread = new Thread(this::sendMessage);
            logger.debug("服务端消息发送线程启动");
            sendMessageThread.start();
        } else {
            logger.debug("Socket未连接，TcpSugarMQServerTransport开启消息发送线程失败！");
        }

    }

    /**
     * 关闭传送通道
     */
    @Override
    public void close() {
        synchronized (isClosed) {
            if (isClosed.get()) {
                return;
            }
            isClosed.set(true);
            logger.debug("TcpSugarMQServerTransport即将被关闭！");
            if (sendMessageThread != null && Thread.State.TERMINATED != sendMessageThread.getState()) {
                sendMessageThread.interrupt();
            }
            if (receiveMessageThread != null && Thread.State.TERMINATED != receiveMessageThread.getState()) {
                receiveMessageThread.interrupt();
            }
            try {
                socket.close();
            } catch (IOException e) {
                logger.info("Socket关闭异常", e);
            }
            sendMessageQueue.clear();
            receiveMessageQueue.clear();
//            tcpSupremeMQTransportCenter.remove(this);
            logger.debug("TcpSugarMQServerTransport已被关闭！");
        }

    }

    @Override
    public BlockingQueue<Message> getSendMessageQueue() {
        return this.sendMessageQueue;
    }

    @Override
    public BlockingQueue<Message> getReceiveMessageQueue() {
        return this.receiveMessageQueue;
    }

    /**
     * 从socket中接收消息
     */
    private void receiveMessage() {
        try {
            ObjectInputStream objectInputStream = null;
            Message message = null;
            Object receiveMessageObject = null;
            while (!Thread.currentThread().isInterrupted() && !socket.isClosed() && !socket.isInputShutdown()) {
//                int byteNum = socket.getInputStream().read(objectByte);
//                if (byteNum <= 0) {
//                    continue;
//                }
//                objectInputStream = new ObjectInputStream(new ByteArrayInputStream(objectByte, 0, byteNum));
                objectInputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                receiveMessageObject = objectInputStream.readObject();

                if (receiveMessageObject == null) {
                    logger.error("【{}】消息为空");
                }
                if (!(receiveMessageObject instanceof Message)) {
                    logger.warn("服务端接收到一个非法消息：" + receiveMessageObject);
                    continue;
                }
                message = (Message) receiveMessageObject;
                logger.info("服务端接收到客户端发来的一条消息:{}", message);
                receiveMessageQueue.put(message);
            }
            logger.error("Socket状态异常，TcpSugarMQServerTransport消息接收线程结束！");
        } catch (Exception e) {
            logger.error("TcpSupremeMQServerTransport消息接收线程错误", e);
        } finally {
            close();
        }
    }

    /**
     * 向socket中发送消息
     */
    private void sendMessage() {
        Message message = null;
//        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        while (true) {
            try {
                message = sendMessageQueue.take();
                logger.debug("即将发送消息：【{}】", message);
            } catch (InterruptedException e) {
                logger.info("TcpSugarMQServerTransport消息发送线程被要求停止！");
                break;
            }
            if (!Thread.currentThread().isInterrupted()
                    && !socket.isClosed()
                    && !socket.isOutputShutdown()) {

                try {
//                    byteArrayOutputStream = new ByteArrayOutputStream();
                    objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
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
                            logger.error("关闭objectOutputStream失败{}");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
