package com.xaut.client.message.bean;

import com.xaut.client.message.Message;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class SupremeMQMapMessage extends Message implements MapMessage {
    private static final long serialVersionUID = 2190580576217128868L;

    public SupremeMQMapMessage() {
        super();
    }

    private ConcurrentHashMap<String, Object> messageMap = new ConcurrentHashMap();

    @Override
    public boolean getBoolean(String s) throws JMSException {
        return false;
    }

    @Override
    public byte getByte(String s) throws JMSException {
        return 0;
    }

    @Override
    public short getShort(String s) throws JMSException {
        return 0;
    }

    @Override
    public char getChar(String s) throws JMSException {
        return 0;
    }

    @Override
    public int getInt(String s) throws JMSException {
        return 0;
    }

    @Override
    public long getLong(String s) throws JMSException {
        return 0;
    }

    @Override
    public float getFloat(String s) throws JMSException {
        return 0;
    }

    @Override
    public double getDouble(String s) throws JMSException {
        return 0;
    }

    @Override
    public String getString(String s) throws JMSException {
        return null;
    }

    @Override
    public byte[] getBytes(String s) throws JMSException {
        return new byte[0];
    }

    @Override
    public Object getObject(String s) throws JMSException {
        return null;
    }

    @Override
    public Enumeration getMapNames() throws JMSException {
        return null;
    }

    @Override
    public void setBoolean(String s, boolean b) throws JMSException {

    }

    @Override
    public void setByte(String s, byte b) throws JMSException {

    }

    @Override
    public void setShort(String s, short i) throws JMSException {

    }

    @Override
    public void setChar(String s, char c) throws JMSException {

    }

    @Override
    public void setInt(String s, int i) throws JMSException {

    }

    @Override
    public void setLong(String s, long l) throws JMSException {

    }

    @Override
    public void setFloat(String s, float v) throws JMSException {

    }

    @Override
    public void setDouble(String s, double v) throws JMSException {

    }

    @Override
    public void setString(String s, String s1) throws JMSException {

    }

    @Override
    public void setBytes(String s, byte[] bytes) throws JMSException {

    }

    @Override
    public void setBytes(String s, byte[] bytes, int i, int i1) throws JMSException {

    }

    @Override
    public void setObject(String s, Object o) throws JMSException {

    }

    @Override
    public boolean itemExists(String s) throws JMSException {
        return false;
    }
}
