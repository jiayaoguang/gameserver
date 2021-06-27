package org.jyg.gameserver.core.field;

import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.ExecTimeUtil;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/6/26
 */
public class UnsafeFieldOperator<T> implements IFieldOperator<T> {

    private static final Unsafe unsafe = initUnsafe();


    private final long offset;

    private final boolean primitive;


    static interface FieldOperatorMethod<T> {
        public Object readObject(Object obj, long offset);

        public void writeObject(Object obj, long offset, T newValue);
    }

    static final Map<Class<?>, FieldOperatorMethod<?>> primitiveFieldOperatorMap;

    static {
        final Map<Class<?>, FieldOperatorMethod<?>> tmpPrimitiveFieldMap = new HashMap<>();
        tmpPrimitiveFieldMap.put(byte.class, new FieldOperatorMethod<Byte>() {
            @Override
            public Object readObject(Object obj, long offset) {
                return unsafe.getByte(obj, offset);
            }

            @Override
            public void writeObject(Object obj, long offset, Byte newValue) {
                unsafe.putByte(obj, offset, newValue);
            }
        });
        tmpPrimitiveFieldMap.put(short.class, new FieldOperatorMethod<Short>() {
            @Override
            public Object readObject(Object obj, long offset) {
                return unsafe.getShort(obj, offset);
            }

            @Override
            public void writeObject(Object obj, long offset, Short newValue) {
                unsafe.putShort(obj, offset, newValue);
            }
        });
        tmpPrimitiveFieldMap.put(int.class, new FieldOperatorMethod<Integer>() {
            @Override
            public Object readObject(Object obj, long offset) {
                return unsafe.getInt(obj, offset);
            }

            @Override
            public void writeObject(Object obj, long offset, Integer newValue) {
                unsafe.putInt(obj, offset, newValue);
            }
        });
        tmpPrimitiveFieldMap.put(long.class, new FieldOperatorMethod<Long>() {
            @Override
            public Object readObject(Object obj, long offset) {
                return unsafe.getLong(obj, offset);
            }

            @Override
            public void writeObject(Object obj, long offset, Long newValue) {
                unsafe.putLong(obj, offset, newValue);
            }
        });
        tmpPrimitiveFieldMap.put(boolean.class, new FieldOperatorMethod<Boolean>() {
            @Override
            public Object readObject(Object obj, long offset) {
                return unsafe.getBoolean(obj, offset);
            }

            @Override
            public void writeObject(Object obj, long offset, Boolean newValue) {
                unsafe.putBoolean(obj, offset, newValue);
            }
        });
        tmpPrimitiveFieldMap.put(char.class, new FieldOperatorMethod<Character>() {
            @Override
            public Object readObject(Object obj, long offset) {
                return unsafe.getChar(obj, offset);
            }

            @Override
            public void writeObject(Object obj, long offset, Character newValue) {
                unsafe.putChar(obj, offset, newValue);
            }
        });
        tmpPrimitiveFieldMap.put(float.class, new FieldOperatorMethod<Float>() {
            @Override
            public Object readObject(Object obj, long offset) {
                return unsafe.getFloat(obj, offset);
            }

            @Override
            public void writeObject(Object obj, long offset, Float newValue) {
                unsafe.putFloat(obj, offset, newValue);
            }
        });
        tmpPrimitiveFieldMap.put(double.class, new FieldOperatorMethod<Double>() {
            @Override
            public Object readObject(Object obj, long offset) {
                return unsafe.getDouble(obj, offset);
            }

            @Override
            public void writeObject(Object obj, long offset, Double newValue) {
                unsafe.putDouble(obj, offset, newValue);
            }
        });

        primitiveFieldOperatorMap = tmpPrimitiveFieldMap;

    }


    private FieldOperatorMethod primitiveFieldOperatorMethod;


    public UnsafeFieldOperator(Field field) {
        this.offset = unsafe.objectFieldOffset(field);
        this.primitive = field.getType().isPrimitive();
        this.primitiveFieldOperatorMethod = primitiveFieldOperatorMap.get(field.getType());
    }


    public boolean readBoolean(T obj) {
        return unsafe.getBoolean(obj, offset);
    }


    public void writeBoolean(T obj, boolean newValue) {
        unsafe.putBoolean(obj, offset, newValue);
    }

    public byte readByte(T obj) {
        return unsafe.getByte(obj, offset);
    }


    public void writeByte(T obj, byte newValue) {
        unsafe.putByte(obj, offset, newValue);
    }


    public short readShort(T obj) {
        return unsafe.getShort(obj, offset);
    }


    public void writeShort(T obj, short newValue) {
        unsafe.putShort(obj, offset, newValue);
    }


    public int readInt(T obj) {
        return unsafe.getInt(obj, offset);
    }


    public void writeInt(T obj, int newValue) {
        unsafe.putInt(obj, offset, newValue);
    }

    @Override
    public long readLong(T obj) {
        return unsafe.getLong(obj, offset);
    }


    public void writeLong(T obj, long newValue) {
        unsafe.putLong(obj, offset, newValue);
    }


    public float readFloat(T obj) {
        return unsafe.getFloat(obj, offset);
    }


    public void writeFloat(T obj, float newValue) {
        unsafe.putFloat(obj, offset, newValue);
    }


    public double readDouble(T obj) {
        return unsafe.getDouble(obj, offset);
    }


    public void writeDouble(T obj, double newValue) {
        unsafe.putDouble(obj, offset, newValue);
    }

    public char readChar(T obj) {
        return unsafe.getChar(obj, offset);
    }


    public void writeChar(T obj, char newValue) {

        unsafe.putChar(obj, offset, newValue);
    }


    @Override
    public T readObject(T obj) {
        if (primitiveFieldOperatorMethod != null) {
            return (T)primitiveFieldOperatorMethod.readObject(obj, offset);
        }

        return (T) unsafe.getObject(obj, offset);
    }

    @Override
    public void writeObject(T obj, Object newValue) {

        if (primitiveFieldOperatorMethod != null) {

            if(newValue == null){
                throw new RuntimeException(" set field fail newValue is null ");
            }

            primitiveFieldOperatorMethod.writeObject(obj, offset, newValue);
            return;
        }
        unsafe.putObject(obj, offset, newValue);
    }


    private static sun.misc.Unsafe initUnsafe() {
        try {
            java.lang.reflect.Field f =
                    sun.misc.Unsafe.class.getDeclaredField("theUnsafe");

            f.setAccessible(true);

            return (sun.misc.Unsafe) f.get(null);
        } catch (Exception e) {
            // ignore
        }

        return sun.misc.Unsafe.getUnsafe();
    }


}
