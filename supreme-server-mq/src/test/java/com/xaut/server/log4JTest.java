package com.xaut.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class log4JTest {
    private static final Logger logger = LoggerFactory.getLogger(log4JTest.class);
    public static void main(String[] args) {
        System.out.println("HelloWorld");
        logger.debug("debug。。。。");
        logger.info("info....");
        logger.error("error....");
    }
}
