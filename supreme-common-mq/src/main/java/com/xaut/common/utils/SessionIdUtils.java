package com.xaut.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.util.Random;

public class SessionIdUtils {
    private static Logger logger = LoggerFactory.getLogger(SessionIdUtils.class);

    /**
     * 返回一个唯一的Session ID
     * @return
     */
    public static String getNewSessionId() throws JMSException {
        String startNum = DateUtils.formatDate(DateUtils.DATE_FORMAT_TYPE3);
        Random random = new Random();
        int next = random.nextInt(1000000);

        String result = startNum + String.format("%1$06d", next);
        logger.debug("生成的会话ID:{}", result);

        return result;
    }
}
