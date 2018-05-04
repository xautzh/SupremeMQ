package com.xaut.common.message.bean;



import com.xaut.common.message.Message;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class SupremeMQMapMessage extends Message implements MapMessage {
    private static final long serialVersionUID = 2190580576217128868L;

    public SupremeMQMapMessage() {
        super();
    }

    private ConcurrentHashMap<String, Object> messageMap = new ConcurrentHashMap();

    @Override
    public boolean getBoolean(String key) throws JMSException {
        return (Boolean) messageMap.get(key);
    }

    @Override
    public byte getByte(String key) throws JMSException {
        return (Byte) messageMap.get(key);
    }

    @Override
    public short getShort(String key) throws JMSException {
        return (Short) messageMap.get(key);
    }

    @Override
    public char getChar(String key) throws JMSException {
        return (Character) messageMap.get(key);
    }

    @Override
    public int getInt(String key) throws JMSException {
        return (Integer) messageMap.get(key);
    }

    @Override
    public long getLong(String key) throws JMSException {
        return (Long) messageMap.get(key);
    }

    @Override
    public float getFloat(String key) throws JMSException {
        return (Float) messageMap.get(key);
    }

    @Override
    public double getDouble(String key) throws JMSException {
        return (Double) messageMap.get(key);
    }

    @Override
    public String getString(String key) throws JMSException {
        return (String) messageMap.get(key);
    }

    @Override
    public byte[] getBytes(String key) throws JMSException {
        return (byte[])messageMap.get(key);
    }

    @Override
    public Object getObject(String key) throws JMSException {
        return messageMap.get(key);
    }

    @Override
    public Enumeration getMapNames() throws JMSException {
        return (Enumeration<String>) messageMap.keySet();
    }

    @Override
    public void setBoolean(String key, boolean value) throws JMSException {
        messageMap.put(key,value);

    }

    @Override
    public void setByte(String key, byte value) throws JMSException {
        messageMap.put(key,value);
    }

    @Override
    public void setShort(String key, short value) throws JMSException {
        messageMap.put(key,value);
    }

    @Override
    public void setChar(String key, char value) throws JMSException {
        messageMap.put(key,value);
    }

    @Override
    public void setInt(String key, int value) throws JMSException {
        messageMap.put(key,value);
    }

    @Override
    public void setLong(String key, long value) throws JMSException {
        messageMap.put(key,value);
    }

    @Override
    public void setFloat(String key, float value) throws JMSException {
        messageMap.put(key,value);
    }

    @Override
    public void setDouble(String key, double value) throws JMSException {
        messageMap.put(key,value);
    }

    @Override
    public void setString(String key, String value) throws JMSException {
        messageMap.put(key,value);
    }

    @Override
    public void setBytes(String key, byte[] value) throws JMSException {
        messageMap.put(key,value);
    }

    @Override
    public void setBytes(String key, byte[] value, int from, int to) throws JMSException {
        messageMap.put(key,Arrays.copyOfRange(value, from, to));
    }

    @Override
    public void setObject(String key, Object value) throws JMSException {
        messageMap.put(key,value);
    }

    @Override
    public boolean itemExists(String key) throws JMSException {
        return messageMap.contains(key);
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
