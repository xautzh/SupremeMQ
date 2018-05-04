package com.xaut.common.message.bean;



import com.xaut.common.message.Message;

import javax.jms.JMSException;
import javax.jms.StreamMessage;

public class SupremeMQStreamMessage extends Message implements StreamMessage {
    private static final long serialVersionUID = 3978761567118765982L;

    @Override
    public boolean readBoolean() throws JMSException {
        return false;
    }

    @Override
    public byte readByte() throws JMSException {
        return 0;
    }

    @Override
    public short readShort() throws JMSException {
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
    public String readString() throws JMSException {
        return null;
    }

    @Override
    public int readBytes(byte[] bytes) throws JMSException {
        return 0;
    }

    @Override
    public Object readObject() throws JMSException {
        return null;
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
    public void writeString(String s) throws JMSException {

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
