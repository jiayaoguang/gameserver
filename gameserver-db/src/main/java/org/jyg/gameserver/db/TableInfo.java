package org.jyg.gameserver.db;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

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
    private LinkedHashMap<String,TableFieldInfo> fieldInfoLinkedMap;

    private Field primaryKeyField;

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

    public LinkedHashMap<String, TableFieldInfo> getFieldInfoLinkedMap() {
        return fieldInfoLinkedMap;
    }

    public void setFieldInfoLinkedMap(LinkedHashMap<String, TableFieldInfo> fieldInfoLinkedMap) {
        this.fieldInfoLinkedMap = fieldInfoLinkedMap;
    }

    public Field getPrimaryKeyField() {
        return primaryKeyField;
    }

    public void setPrimaryKeyField(Field primaryKeyField) {
        this.primaryKeyField = primaryKeyField;
    }
}
