package org.jyg.gameserver.db;

import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/14
 */
public class TableInfo {
    /**
     * 对应的类名
     */
    private Class<?> dbEntityClass;
    /**
     * 表明
     */
    private String tableName;
    /**
     * 主键名
     */
    private String primaryKey = "id";
    /**
     * 字段列表
     */
    private Map<String,TableFieldInfo> fieldInfoMap;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Class<?> getDbEntityClass() {
        return dbEntityClass;
    }

    public void setDbEntityClass(Class<?> dbEntityClass) {
        this.dbEntityClass = dbEntityClass;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Map<String, TableFieldInfo> getFieldInfoMap() {
        return fieldInfoMap;
    }

    public void setFieldInfoMap(Map<String, TableFieldInfo> fieldInfoMap) {
        this.fieldInfoMap = fieldInfoMap;
    }
}
