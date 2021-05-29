package org.jyg.gameserver.db.type;

import org.jyg.gameserver.db.type.*;

import java.util.HashMap;
import java.util.Map;

public class TypeHandlerRegistry {


    private final Map<Class<?>, TypeHandler<?>> typeHandlerMap = new HashMap<>();


    public TypeHandlerRegistry() {

        registerTypeHandler(Byte.class, new ByteTypeHandler());
        registerTypeHandler(byte.class, typeHandlerMap.get(Byte.class));

        registerTypeHandler(Short.class, new ShortTypeHandler());
        registerTypeHandler(short.class, typeHandlerMap.get(Short.class));

        registerTypeHandler(Integer.class, new IntegerTypeHandler());
        registerTypeHandler(int.class, typeHandlerMap.get(Integer.class));

        registerTypeHandler(Long.class, new LongTypeHandler());
        registerTypeHandler(long.class, typeHandlerMap.get(Long.class));

        registerTypeHandler(Float.class, new FloatTypeHandler());
        registerTypeHandler(float.class, typeHandlerMap.get(Float.class));

        registerTypeHandler(Double.class, new DoubleTypeHandler());
        registerTypeHandler(double.class, typeHandlerMap.get(Double.class));

        registerTypeHandler(Character.class, new CharacterTypeHandler());
        registerTypeHandler(char.class, typeHandlerMap.get(Character.class));

        registerTypeHandler(Boolean.class, new BooleanTypeHandler());
        registerTypeHandler(boolean.class, typeHandlerMap.get(Boolean.class));

        registerTypeHandler(String.class, new StringTypeHandler());

    }

    public void registerTypeHandler(Class<?> clazz, TypeHandler<?> typeHandler) {
        if (typeHandlerMap.containsKey(clazz)) {
            throw new RuntimeException("containsKey");
        }

        typeHandlerMap.put(clazz, typeHandler);
    }



    public TypeHandler<?> getTypeHandler(Class<?> clazz) {
        return typeHandlerMap.get(clazz);
    }

}
