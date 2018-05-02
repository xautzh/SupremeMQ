package com.xaut.client.transport;

import com.xaut.client.transport.tcp.TcpMessageTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SupremeMQTransportFactory {

    //使用正则表达式判断url
    // 解析提供者URL的正则表达式字符串
    private final static String REGEX_STR = "([a-z]{3})://([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}):([0-9]{1,6})";

    private static Logger logger = LoggerFactory.getLogger(SupremeMQTransportFactory.class);

    public static SupremeMQTransport createSupremeMQTransport(String url) throws JMSException {
        if (url == null) {
            logger.error("url为空");
            throw new JMSException("提供者url为空");
        }
        Pattern pattern = Pattern.compile(REGEX_STR);
        Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
            logger.error("url格式错误");
            throw new JMSException("url格式错误");
        }
        String transportType = matcher.group(1);
        String ipAddress = matcher.group(2);
        int port = Integer.parseInt(matcher.group(3));

        String[] ipAddressArray = ipAddress.trim().split("\\.");
        byte[] ipBytes = new byte[]{
                (byte) Integer.parseInt(ipAddressArray[0]),
                (byte) Integer.parseInt(ipAddressArray[1]),
                (byte) Integer.parseInt(ipAddressArray[2]),
                (byte) Integer.parseInt(ipAddressArray[3])};
        //协议 tcp 。。。。后期使用枚举列出
        if (transportType.equals("tcp")) {
            try {
                TcpMessageTransport tcpMessageTransport = new TcpMessageTransport(InetAddress.getByAddress(ipBytes), port);
                return tcpMessageTransport;
            } catch (UnknownHostException e) {
                logger.error(e.getMessage());
                throw new JMSException(e.getMessage());
            }
        } else if (transportType.equals("其他的协议")) {
            //TO DO
        }
        throw new JMSException("无法找到匹配的传输器类型：" + url);
    }
}
