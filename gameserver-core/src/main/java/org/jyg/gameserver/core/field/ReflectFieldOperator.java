package org.jyg.gameserver.core.field;

import java.lang.reflect.Field;

/**
 * create by jiayaoguang at 2021/6/26
 */
public class ReflectFieldOperator<T> implements IFieldOperator<T> {

    private final Field field;

    public ReflectFieldOperator(Field field) {
        field.setAccessible(true);
        this.field = field;
    }

    @Override
    public boolean readBoolean(T obj) {

        try {
            return field.getBoolean(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void writeBoolean(T obj, boolean newValue) {
        try {
            field.setBoolean(obj , newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte readByte(T obj) {
        try {
            return field.getByte(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeByte(T obj, byte newValue) {
        try {
            field.setByte(obj , newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public short readShort(T obj) {
        try {
            return field.getShort(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeShort(T obj, short newValue) {
        try {
            field.setShort(obj , newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int readInt(T obj) {
        try {
            return field.getInt(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeInt(T obj, int newValue) {
        try {
            field.setInt(obj , newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long readLong(T obj){
        try {
            return field.getLong(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeLong(T obj, long newValue) {
        try {
            field.setLong(obj , newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public float readFloat(T obj) {
        try {
            return field.getFloat(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeFloat(T obj, float newValue) {
        try {
            field.setFloat(obj , newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double readDouble(T obj) {
        try {
            return field.getDouble(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeDouble(T obj, double newValue) {
        try {
            field.setDouble(obj , newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public char readChar(T obj) {
        try {
            return field.getChar(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeChar(T obj, char newValue) {
        try {
            field.setChar(obj , newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T readObject(T obj) {
        try {
            return (T)field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeObject(T obj, Object newValue) {
        try {
            field.set(obj , newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getFieldName() {
        return field.getType().getSimpleName();
    }
}
