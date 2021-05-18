package org.jyg.gameserver.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * create by jiayaoguang at 2021/5/14
 */
public class TableFieldInfo {

    private String tableFieldName;
    private int length;
    private TableFieldType fieldType;
    private Field classField;

    private Method fiedGetMethod;


    public String getTableFieldName() {
        return tableFieldName;
    }

    public void setTableFieldName(String tableFieldName) {
        this.tableFieldName = tableFieldName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public TableFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(TableFieldType fieldType) {
        this.fieldType = fieldType;
    }

    public Field getClassField() {
        return classField;
    }

    public void setClassField(Field classField) {
        this.classField = classField;
    }


    public Method getFiedGetMethod() {
        return fiedGetMethod;
    }

    public void setFiedGetMethod(Method fiedGetMethod) {
        this.fiedGetMethod = fiedGetMethod;
    }
}
