package org.jyg.gameserver.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * create by jiayaoguang on 2023/4/21
 */
public class ClassUtil {

    public static Type getClassGenericType(Class<Object> clazz){
        Type superClass = clazz.getGenericSuperclass();
        if (superClass instanceof Class<?>) { // sanity check, should never happen
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }

        Type _type = ((ParameterizedType) superClass).getActualTypeArguments()[0];

        return _type;
    }



    public static Field getClassObjectField(Class<?> clazz , String fieldName){


        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            //ignore
        }
        if(field != null){
            return field;
        }


        Class<?> superClass = clazz.getSuperclass();

        for(;superClass != null;){
            try {
                field = superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                //ignore
            }

            superClass = superClass.getSuperclass();
        }


        return field;
    }


    public static boolean isStatic(Field field){
        return Modifier.isStatic(field.getModifiers());
    }


    public static List<Field> getClassObjectFields(Class<?> clazz){

        List<Field> classObjectFields = new ArrayList<>();


        for(Class<?> superClass : getSuperClasses(clazz)){
            Field[] superClassFields = superClass.getDeclaredFields();
            for(Field field : superClassFields){
                if(isStatic(field)){
                    continue;
                }
                classObjectFields.add(field);
            }
        }


        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            if(isStatic(field)){
                continue;
            }
            classObjectFields.add(field);
        }


        return classObjectFields;
    }


    public static Queue<Class<?>> getSuperClasses(Class<?> clazz){
        Deque<Class<?>> superClasses = new ArrayDeque<>(5);
        Class<?> superClass = clazz.getSuperclass();

        for(;superClass != null;){
            superClasses.addLast(superClass);
            superClass = superClass.getSuperclass();
        }
        return superClasses;
    }


}
