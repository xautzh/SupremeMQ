package com.xaut.server.vo;

public class QueueVo {
    //消息队列名称
    private String queueName;
    //生产了多少条消息
    private Integer providerMessageNumber;
    //待消费数目
    private Integer waitingMessageNumber;
    //已经消费的数目
    private Integer consumedMessage;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Integer getProviderMessageNumber() {
        return providerMessageNumber;
    }

    public void setProviderMessageNumber(Integer providerMessageNumber) {
        this.providerMessageNumber = providerMessageNumber;
    }

    public Integer getWaitingMessageNumber() {
        return waitingMessageNumber;
    }

    public void setWaitingMessageNumber(Integer waitingMessageNumber) {
        this.waitingMessageNumber = waitingMessageNumber;
    }

    public Integer getConsumedMessage() {
        return consumedMessage;
    }

    public void setConsumedMessage(Integer consumedMessage) {
        this.consumedMessage = consumedMessage;
    }

    @Override
    public String toString() {
        return "QueueVo{" +
                "queueName='" + queueName + '\'' +
                ", providerMessageNumber=" + providerMessageNumber +
                ", waitingMessageNumber=" + waitingMessageNumber +
                ", consumedMessage=" + consumedMessage +
                '}';
    }
}
