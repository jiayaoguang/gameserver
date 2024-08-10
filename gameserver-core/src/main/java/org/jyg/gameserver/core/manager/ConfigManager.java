package org.jyg.gameserver.core.manager;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSONArray;
import org.jyg.gameserver.core.util.Logs;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * create by jiayaoguang on 2024/8/10
 */
public class ConfigManager implements Lifecycle {

    public String configDirName = "config";

    public String idGetMethodName = "getSn";


    private final Map<Class<?>, Map<Integer, Object>> allConfMap = new HashMap<>(128, 0.5f);


    private final Map<String, Map<Object, Object>> allIndexConfMap = new HashMap<>(128, 0.5f);


    public ConfigManager() {
    }

    public String getConfigDirName() {
        return configDirName;
    }

    public void setConfigDirName(String configDirName) {
        this.configDirName = configDirName;
    }


    public String getIdGetMethodName() {
        return idGetMethodName;
    }

    public void setIdGetMethodName(String idGetMethodName) {
        this.idGetMethodName = idGetMethodName;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {


    }

    /**
     * 加载配置
     * 启动完成后只能在主线程调用
     */
    public void loadConfig(Class<?> configClazz) {
        String configFilePath = configDirName + File.separator + configClazz.getSimpleName() + ".json";
        String jsonStr = FileUtil.readString(new File(configFilePath), StandardCharsets.UTF_8);
        loadConfig(jsonStr, configClazz);
    }

    /**
     * 加载配置
     * 启动完成后只能在主线程调用
     */
    private void loadConfig(String jsonStr, Class<?> jsonClazz) {
        List<?> objArray = JSONArray.parseArray(jsonStr, jsonClazz);

        Map<Integer, Object> confObjMap = new LinkedHashMap<>();
        for (Object confObj : objArray) {
            try {
                Method getIdMethod = confObj.getClass().getMethod(idGetMethodName);
                Object idObj = getIdMethod.invoke(confObj);

                if (!(idObj instanceof Integer)) {
                    throw new RuntimeException("sn must int , conf:" + jsonClazz.getSimpleName());
                }
                int id = (Integer) idObj;
                if (confObjMap.containsKey(id)) {
                    throw new RuntimeException(jsonClazz.getSimpleName() + " sheet dup conf sn : " + id);
                }
                confObjMap.put(id, confObj);

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        allConfMap.put(jsonClazz, Collections.unmodifiableMap(confObjMap));
        Logs.DEFAULT_LOGGER.info("load {} config success , lines {}", jsonClazz.getSimpleName(), confObjMap.size());
    }


    public <C> C getConf(Class<C> c, int sn) {
        Map<Integer, Object> confObjMap = allConfMap.get(c);
        if (confObjMap == null) {
            return null;
        }
        return (C) confObjMap.get(sn);

    }


    public <C> List<C> getConfs(Class<C> c) {
        Map<Integer, Object> confObjMap = allConfMap.get(c);
        if (confObjMap == null) {
            return Collections.EMPTY_LIST;
        }
        return (List<C>) new ArrayList<>(confObjMap.values());
    }


    /**
     * 给表格字段加索引
     * 启动完后只能在主线程调用
     */
    public <C> void addConfIndex(Class<C> confClazz, String indexFieldName) {
        String indexKey = getFiledIndexName(confClazz, indexFieldName);

        if (allIndexConfMap.containsKey(indexKey)) {
            throw new RuntimeException("dup conf index : " + indexKey);
        }

        //忽略布尔类型的get方法
        String getMethodName = "get" + indexFieldName.substring(0, 1).toUpperCase();
        if (indexFieldName.length() > 1) {
            getMethodName += indexFieldName.substring(1);
        }

        List<C> confs = getConfs(confClazz);

        Map<Object, Object> indexConfMap = new LinkedHashMap<>();

        for (C conf : confs) {

            try {
                Method getMethod = conf.getClass().getMethod(getMethodName);
                Object indexValue = getMethod.invoke(conf);

                if (indexConfMap.containsKey(indexValue)) {
                    throw new RuntimeException("dup conf index : " + indexKey + " value : " + indexValue);
                }

                indexConfMap.put(indexValue, conf);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        Logs.DEFAULT_LOGGER.info("add config table {} index {}", confClazz.getSimpleName(), indexKey);
        allIndexConfMap.put(indexKey, Collections.unmodifiableMap(indexConfMap));
    }

    public <C> C getConfByIndex(Class<C> confClazz, String indexFieldName, Object value) {
        String indexKey = getFiledIndexName(confClazz, indexFieldName);


        Map<Object, Object> indexConfMap = allIndexConfMap.get(indexKey);

        if (indexConfMap == null) {
            Logs.DEFAULT_LOGGER.error("getConfByIndex fail ,not have index {}", indexKey);
            return null;
        }

        return (C) indexConfMap.get(value);
    }

    private String getFiledIndexName(Class<?> confClazz, String indexFieldName) {
        return confClazz.getSimpleName() + "_" + indexFieldName;
    }

}

