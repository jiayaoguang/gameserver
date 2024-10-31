package org.jyg.gameserver.core.util;


import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.util.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * create by jiayaoguang at 2021/5/22
 */
public class ConfigUtil {

    private static final Logger CONFIG = LoggerFactory.getLogger("config");

    private ConfigUtil() {

    }

    public static <T> T properties2Object(final String fileName, final Class<T> configClazz) {
        T object;

        try {
            object = configClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }

        properties2Object(fileName , object);

        return object;
    }


    public static void properties2Object( String fileName, final Object object) {
        if(fileName.indexOf('.') < 0){
            fileName += ".properties";
        }

        File file = new File(fileName);

        if (!file.exists()) {
            Logs.DEFAULT_LOGGER.error(" config {} file not exist ", fileName);
            return;
        }

        if (file.isDirectory()) {
            Logs.DEFAULT_LOGGER.error(" config {} file isDirectory ", fileName);
            return;
        }


        try {
//            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            Properties properties = new Properties();
            try (InputStream in = new BufferedInputStream(new FileInputStream(file));) {
                properties.load(in);
            }
            properties2Object(properties, object);
        } catch (IOException e) {
            Logs.DEFAULT_LOGGER.error("make exception : " ,e);
            throw new IllegalArgumentException(e);
        }
    }

    public static void properties2Object(final Properties p, final Object object) {
        List<Field> fields = ClassUtil.getClassObjectFields(object.getClass());
        for (Field field : fields) {
            String fieldName = field.getName();

            try {

                String key = fieldName;
                String privateKey = object.getClass().getSimpleName() + "." + fieldName;
                String privateProperty = p.getProperty(privateKey);
                if (privateProperty != null) {
                    key = privateKey;
                }
                String property = p.getProperty(key);
                if (property == null) {
//                    println("property == null");
                    continue;
                }

                property = property.trim();

                String cn = field.getType().getSimpleName();
                Object arg = null;
                switch (cn) {
                    case "int":
                    case "Integer":
                        arg = Integer.parseInt(property);
                        break;
                    case "long":
                    case "Long":
                        arg = Long.parseLong(property);
                        break;
                    case "double":
                    case "Double":
                        arg = Double.parseDouble(property);
                        break;
                    case "boolean":
                    case "Boolean":
                        arg = Boolean.parseBoolean(property);
                        break;
                    case "float":
                    case "Float":
                        arg = Float.parseFloat(property);
                        break;
                    case "String":
                        arg = property;
                        break;
                    case "String[]": {
                        String[] strArray = property.split(";");
                        for (int i = 0; i < strArray.length; i++) {
                            String str = strArray[i];
                            strArray[i] = str.trim();
                            if (StringUtils.isEmpty(strArray[i])) {
                                throw new IllegalArgumentException(" conf String[] key : " + key + " contains empty string");
                            }
                        }
                        arg = strArray;
                        break;
                    }
                    case "int[]": {
                        String[] strArray = property.split(";");
                        int[] intArray = new int[strArray.length];
                        for (int i = 0; i < strArray.length; i++) {
                            String str = strArray[i];
                            intArray[i] = Integer.parseInt(str.trim());
                        }
                        arg = intArray;
                        break;
                    }
                    case "Set":{
                        String[] strArray = property.split(";");
                        Set<String> set = new LinkedHashSet<>();
                        for(String str : strArray ){
                            String content = str.trim();
                            if (StringUtils.isEmpty(content)) {
                                throw new IllegalArgumentException(" conf Set key : " + content + " contains empty string");
                            }
                            set.add(content);
                        }
                        arg = set;
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("unknown config field type : " + cn);
                }
                Logs.DEFAULT_LOGGER.info("set field : {} , value : {} ", key, arg);

                Method fieldGetMethod = getFieldGetMethod(object.getClass() , field);

                if(fieldGetMethod != null){
                    fieldGetMethod.invoke(object , arg);
                }else {
                    field.setAccessible(true);
                    field.set(object, arg);
                }

            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public static Method getFieldGetMethod(Class<?> clazz, Field field) {
        String fieldName = field.getName();
        String setMethodName;
        if(fieldName.length() == 1){
            setMethodName = "set" + fieldName.substring(0 , 1).toUpperCase();
        }else {
            setMethodName = "set" + fieldName.substring(0 , 1).toUpperCase() + fieldName.substring(1);
        }

        try {
            Method method = clazz.getDeclaredMethod(setMethodName , field.getType());
            return method;
        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
            return null;
        }


    }


}

