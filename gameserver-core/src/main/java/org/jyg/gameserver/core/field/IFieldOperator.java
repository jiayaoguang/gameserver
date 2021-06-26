package org.jyg.gameserver.core.field;

import java.lang.reflect.Field;

/**
 * create by jiayaoguang at 2021/6/26
 */
public interface IFieldOperator<T> {


    public boolean readBoolean(T obj);


    public void writeBoolean(T obj , boolean newValue);

    public byte readByte(T obj);


    public void writeByte(T obj , byte newValue);


    public short readShort(T obj);


    public void writeShort(T obj , short newValue);


    public int readInt(T obj);


    public void writeInt(T obj , int newValue);

    public long readLong(T obj);


    public void writeLong(T obj , long newValue);


    public float readFloat(T obj);

    public void writeFloat(T obj , float newValue);


    public double readDouble(T obj);


    public void writeDouble(T obj , double newValue);

    public char readChar(T obj);


    public void writeChar(T obj , char newValue);


    public T readObject(T obj);

    public void writeObject(T obj , Object newValue);
}
