package com.xaut.common.constant;

public enum MessageContainerType {
    QUEUE("QUEUE"),	// 队列
    TOPIC("TOPIC");	// 主题

    String value;
    private MessageContainerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
