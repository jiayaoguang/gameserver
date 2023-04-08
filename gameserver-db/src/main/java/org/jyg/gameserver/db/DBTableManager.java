package org.jyg.gameserver.db;

import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.field.UnsafeFieldOperator;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.db.anno.DBTable;
import org.jyg.gameserver.db.anno.DBTableField;
import org.jyg.gameserver.db.anno.DBTableFieldIgnore;
import org.jyg.gameserver.db.type.TypeHandlerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang on 2021/6/12
 */
public class DBTableManager {

    private final Map<Class<?>, TableInfo> tableInfoMap = new HashMap<>(1024, 0.5f);

    private final TypeHandlerRegistry typeHandlerRegistry;

    public DBTableManager(TypeHandlerRegistry typeHandlerRegistry) {
        this.typeHandlerRegistry = typeHandlerRegistry;
    }


    public TableInfo tryAddTableInfo(Class<?> dbEntityClass) {

        TableInfo tableInfo = getTableInfo(dbEntityClass);
        if(tableInfo != null){
            return tableInfo;
        }

        return addTableInfo(dbEntityClass);

    }

    private TableInfo addTableInfo(Class<?> dbEntityClass) {
        if (tableInfoMap.containsKey(dbEntityClass)) {
            throw new IllegalArgumentException(" addTableInfo fail contains dbEntityClass " + dbEntityClass.getCanonicalName());
        }
        TableInfo tableInfo = null;
        try {
            tableInfo = createTableInfo(dbEntityClass);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        tableInfoMap.put(dbEntityClass, tableInfo);
        return tableInfo;
    }


    /**
     * @param dbEntityClass dbEntityClass
     * @return TableInfo
     */
    private TableInfo createTableInfo(Class<?> dbEntityClass) throws NoSuchFieldException, NoSuchMethodException {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setDbEntityClass(dbEntityClass);

        DBTable dbTableAnno = dbEntityClass.getAnnotation(DBTable.class);
        if (dbTableAnno != null) {
            tableInfo.setDbTableAnno(dbTableAnno);
            tableInfo.setTableName(dbTableAnno.tableName());
            tableInfo.setPrimaryKey(dbTableAnno.primaryKey());
        } else {
            tableInfo.setTableName(dbEntityClass.getSimpleName());
            tableInfo.setPrimaryKey("id");
        }


        List<Field> allObjectFields = AllUtil.getClassObjectFields(dbEntityClass);


        LinkedHashMap<String, TableFieldInfo> tableFieldInfoMap = new LinkedHashMap<>();

        for (Field field : allObjectFields) {

            if (field.getAnnotation(DBTableFieldIgnore.class) != null) {
                continue;
            }
            //忽略静态字段
            if (AllUtil.isStatic(field)) {
                continue;
            }

            if (tableFieldInfoMap.containsKey(field.getName())) {
                throw new IllegalArgumentException("class " + dbEntityClass.getCanonicalName() + " duplicate field " + field.getName());
            }

            if(typeHandlerRegistry != null && typeHandlerRegistry.getTypeHandler(field.getType()) == null){
                throw new IllegalArgumentException("field type not support : " + field.getType().getCanonicalName());
            }

            field.setAccessible(true);
            TableFieldInfo tableFieldInfo = createTableFieldInfo(dbEntityClass, field);
            tableFieldInfoMap.put(field.getName(), tableFieldInfo);

        }

        tableInfo.setFieldInfoLinkedMap(tableFieldInfoMap);


        TableFieldInfo primaryKeyTableField = tableFieldInfoMap.get(tableInfo.getPrimaryKey());

        tableInfo.setPrimaryKeyField(primaryKeyTableField.getClassField());
        tableInfo.setPrimaryKeyFieldInfo(primaryKeyTableField);

        return tableInfo;
    }

    private TableFieldInfo createTableFieldInfo(Class<?> dbEntityClass, Field dbEntityField){
        TableFieldInfo tableFieldInfo = new TableFieldInfo();
        tableFieldInfo.setClassField(dbEntityField);
        tableFieldInfo.setFieldOperator(new UnsafeFieldOperator<>(dbEntityField));
//        dbEntityField.setAccessible(true);

        DBTableField dbTableFieldAnno = dbEntityField.getAnnotation(DBTableField.class);
        if (dbTableFieldAnno != null) {
            tableFieldInfo.setDbTableFieldAnno(dbTableFieldAnno);
            String fieldName;
            if(StringUtils.isNotEmpty(dbTableFieldAnno.fieldName())){
                fieldName = dbTableFieldAnno.fieldName();
            }else {
                fieldName = dbEntityField.getName();
            }
            tableFieldInfo.setTableFieldName(fieldName);
            if (dbTableFieldAnno.fieldType() == TableFieldType.AUTO) {
                TableFieldType tableFieldType = getTableFieldType(dbEntityField.getType());
                tableFieldInfo.setFieldType(tableFieldType);
            } else {
                tableFieldInfo.setFieldType(dbTableFieldAnno.fieldType());
            }
            if (dbTableFieldAnno.fieldLength() > 0) {
                tableFieldInfo.setLength(dbTableFieldAnno.fieldLength());
            } else {
                tableFieldInfo.setLength(dbTableFieldAnno.fieldType().getDefaultLength());
            }
        } else {
            tableFieldInfo.setTableFieldName(dbEntityField.getName());

            TableFieldType tableFieldType = getTableFieldType(dbEntityField.getType());
            tableFieldInfo.setFieldType(tableFieldType);
            tableFieldInfo.setLength(tableFieldInfo.getFieldType().getDefaultLength());

        }


        String fieldGetMethodName = getFieldGetMethodName(dbEntityField);

        try {
            Method fiedGetMethod = dbEntityClass.getMethod(fieldGetMethodName);
            tableFieldInfo.setFieldGetMethod(fiedGetMethod);
        } catch (Exception e) {
            //ignore
        }


        return tableFieldInfo;
    }


    public TableFieldType getTableFieldType(Class<?> fieldClass) {
        if (fieldClass == int.class || fieldClass == Integer.class) {
            return TableFieldType.INTEGER;
        } else if (fieldClass == String.class) {
            return TableFieldType.VARCHAR;
        } else if (fieldClass == long.class || fieldClass == Long.class) {
            return TableFieldType.BIGINT;
        } else if (fieldClass == short.class || fieldClass == Short.class) {
            return TableFieldType.SMALLINT;
        } else if (fieldClass == float.class || fieldClass == Float.class) {
            return TableFieldType.FLOAT;
        } else if (fieldClass == double.class || fieldClass == Double.class) {
            return TableFieldType.DOUBLE;
        } else if (fieldClass == byte.class || fieldClass == Byte.class) {
            return TableFieldType.TINYINT;
        } else if (fieldClass == boolean.class || fieldClass == Boolean.class) {
            return TableFieldType.BOOLEAN;
        } else if (fieldClass == char.class || fieldClass == Character.class) {
            //char 类型对应数据库定长字符串
            return TableFieldType.CHAR;
        }
//        else if (typeHandlerMap.containsKey(fieldClass)) {
//            return TableFieldType.VARCHAR;
//        }
        else {
            throw new IllegalArgumentException(fieldClass.getName() + " field type can not to sql type");
        }

//            TypeHandler<?> typeHandler = typeHandlerMap.get(fieldClass);
//            if (typeHandler != null) {
//                return typeHandler.getTableFieldType();
//            }
//
//            throw new IllegalArgumentException(fieldClass.getName() + " field type can not to sql type");
//        }
    }

    private String getFieldGetMethodName(Field dbEntityField) {
        String fieldGetMethodName;


//            if (dbEntityField.getName().startsWith("is")) {
//
//            }

        if (dbEntityField.getName().length() <= 1) {
            fieldGetMethodName = "get" + dbEntityField.getName().substring(0, 1).toUpperCase();
        } else {
            fieldGetMethodName = "get" + dbEntityField.getName().substring(0, 1).toUpperCase() + dbEntityField.getName().substring(1);
        }

        return fieldGetMethodName;
    }

    public TableInfo getTableInfo(Class<?> dbClazz) {
        return tableInfoMap.get(dbClazz);
    }
}
