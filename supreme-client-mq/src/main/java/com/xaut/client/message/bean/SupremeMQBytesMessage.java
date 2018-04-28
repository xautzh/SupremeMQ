package com.xaut.client.message.bean;

import com.xaut.client.message.Message;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

public class SupremeMQBytesMessage extends Message implements BytesMessage {

    private static final long serialVersionUID = -8641306528891219928L;

    public SupremeMQBytesMessage() {
        super();
    }

    @Override
    public long getBodyLength() throws JMSException {
        return 0;
    }

    @Override
    public boolean readBoolean() throws JMSException {
        return false;
    }

    @Override
    public byte readByte() throws JMSException {
        return 0;
    }

    @Override
    public int readUnsignedByte() throws JMSException {
        return 0;
    }

    @Override
    public short readShort() throws JMSException {
        return 0;
    }

    @Override
    public int readUnsignedShort() throws JMSException {
        return 0;
    }

    @Override
    public char readChar() throws JMSException {
        return 0;
    }

    @Override
    public int readInt() throws JMSException {
        return 0;
    }

    @Override
    public long readLong() throws JMSException {
        return 0;
    }

    @Override
    public float readFloat() throws JMSException {
        return 0;
    }

    @Override
    public double readDouble() throws JMSException {
        return 0;
    }

    @Override
    public String readUTF() throws JMSException {
        return null;
    }

    @Override
    public int readBytes(byte[] bytes) throws JMSException {
        return 0;
    }

    @Override
    public int readBytes(byte[] bytes, int i) throws JMSException {
        return 0;
    }

    @Override
    public void writeBoolean(boolean b) throws JMSException {

    }

    @Override
    public void writeByte(byte b) throws JMSException {

    }

    @Override
    public void writeShort(short i) throws JMSException {

    }

    @Override
    public void writeChar(char c) throws JMSException {

    }

    @Override
    public void writeInt(int i) throws JMSException {

    }

    @Override
    public void writeLong(long l) throws JMSException {

    }

    @Override
    public void writeFloat(float v) throws JMSException {

    }

    @Override
    public void writeDouble(double v) throws JMSException {

    }

    @Override
    public void writeUTF(String s) throws JMSException {

    }

    @Override
    public void writeBytes(byte[] bytes) throws JMSException {

    }

    @Override
    public void writeBytes(byte[] bytes, int i, int i1) throws JMSException {

    }

    @Override
    public void writeObject(Object o) throws JMSException {

    }

    @Override
    public void reset() throws JMSException {

    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        return new byte[0];
    }

    @Override
    public void setJMSCorrelationIDAsBytes(byte[] bytes) throws JMSException {

    }

    @Override
    public void clearProperties() throws JMSException {

    }

    @Override
    public void acknowledge() throws JMSException {

    }

    @Override
    public void clearBody() throws JMSException {

    }
}
