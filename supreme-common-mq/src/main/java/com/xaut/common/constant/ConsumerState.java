package com.xaut.common.constant;

public enum ConsumerState {
    CREATE("0"),
    WORKING("1"),
    DEATH("2");

    String value;
    private ConsumerState(String value) {
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
