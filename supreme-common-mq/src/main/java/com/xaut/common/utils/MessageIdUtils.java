package com.xaut.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.util.Random;

public class MessageIdUtils {
    private static Logger logger = LoggerFactory.getLogger(MessageIdUtils.class);

    /**
     * 返回一个唯一的消息ID
     * @return
     */
    public static String getNewMessageId() throws JMSException {
        String startNum = DateUtils.formatDate(DateUtils.DATE_FORMAT_TYPE3);
        Random random = new Random();
        int next = random.nextInt(100000000);

        String result = startNum + String.format("%1$08d", next);
        logger.debug("生成的消息ID:{}", result);

        return result;
    }

}
