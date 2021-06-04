package org.jyg.gameserver.db;

import cn.hutool.core.collection.CollectionUtil;
import org.jyg.gameserver.core.consumer.BlockingQueueConsumer;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.db.anno.DBTable;
import org.jyg.gameserver.db.anno.DBTableField;
import org.jyg.gameserver.db.anno.DBTableFieldIgnore;
import org.jyg.gameserver.db.type.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/16
 */
public class DBConsumer extends BlockingQueueConsumer {

    private final SqlKeyWord sqlKeyWord;

    private final Map<Integer, SQLMaker> sqlTextMap = new HashMap<>(MAP_DEFAULT_SIZE, MAP_DEFAULT_LOADFACTOR);

    private final Map<Class<?>, TableInfo> tableInfoMap = new HashMap<>(MAP_DEFAULT_SIZE, MAP_DEFAULT_LOADFACTOR);

    private final DBConfig dbConfig;

    private final SimpleDataSource dataSource;
    private final TypeHandlerRegistry typeHandlerRegistry;

    private final SqlExecutor sqlExecutor;


//    public DBConsumer() {
//        this(null);
//
//    }

    public DBConsumer(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
        this.sqlKeyWord = new MySQLUpperKey();
        this.dataSource = new SimpleDataSource(dbConfig);
        this.typeHandlerRegistry = new TypeHandlerRegistry();
        this.sqlExecutor = new SqlExecutor(dataSource , typeHandlerRegistry);

        init();

    }

    private void init() {
        addSQLMaker(BDEventConst.INSERT, new InsertSQLMaker());
        addSQLMaker(BDEventConst.DELETE, new DeleteSQLMaker());
        addSQLMaker(BDEventConst.UPDATE, new UpdateSQLMaker());
        addSQLMaker(BDEventConst.SELECT, new SelectSQLMaker());

        addSQLMaker(BDEventConst.SELECT_BY_FIELD, new SelectByFieldSQLMaker());


//        registerTypeHandler(new StringTypeHandler());
//
//        registerTypeHandler(new LongTypeHandler());
//        registerTypeHandler(new IntegerTypeHandler());
//        registerTypeHandler(new ShortTypeHandler());
//        registerTypeHandler(new ByteTypeHandler());
//        registerTypeHandler(new DoubleTypeHandler());
//        registerTypeHandler(new FloatTypeHandler());
//        registerTypeHandler(new CharacterTypeHandler());
//        registerTypeHandler(new BooleanTypeHandler());


    }

    public void registerTypeHandler(Class<?> clazz , TypeHandler<?> typeHandler) {
        typeHandlerRegistry.registerTypeHandler(clazz,typeHandler);
    }

    public TypeHandler<?> getTypeHandler(Class<?> clazz) {
        return typeHandlerRegistry.getTypeHandler(clazz);
    }


    public void addSQLMaker(int eventId, SQLMaker SQLMaker) {
        if (sqlTextMap.containsKey(eventId)) {
            throw new IllegalArgumentException(" addDBProcessor fail contains eventId " + eventId);
        }
        sqlTextMap.put(eventId, SQLMaker);
    }

    public TableInfo addTableInfo(Class<?> dbEntityClass) {
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

            if(typeHandlerRegistry.getTypeHandler(field.getType()) == null){
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

    private TableFieldInfo createTableFieldInfo(Class<?> dbEntityClass, Field dbEntityField) throws NoSuchFieldException, NoSuchMethodException {
        TableFieldInfo tableFieldInfo = new TableFieldInfo();
        tableFieldInfo.setClassField(dbEntityField);
//        dbEntityField.setAccessible(true);

        DBTableField dbTableFieldAnno = dbEntityField.getAnnotation(DBTableField.class);
        if (dbTableFieldAnno != null) {
            tableFieldInfo.setDbTableFieldAnno(dbTableFieldAnno);
            tableFieldInfo.setTableFieldName(dbTableFieldAnno.fieldName());
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
            tableFieldInfo.setFiedGetMethod(fiedGetMethod);
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

    @Override
    public void beforeStart() {
        sqlExecutor.tryConnectIfClose();
    }

    @Override
    public void doStop() {
        sqlExecutor.closeQuiet();
    }


    @Override
    protected void processDefaultEvent(int eventId, Object dbEntity, EventData eventData) {
        SQLMaker sqlMaker = sqlTextMap.get(eventId);

        if (sqlMaker == null) {
            Logs.DEFAULT_LOGGER.error(" unknow db event type {} ", eventId);
            return;
        }


        TableInfo tableInfo = tableInfoMap.get(dbEntity.getClass());
        if (tableInfo == null) {
            Logs.DEFAULT_LOGGER.info(" unknow tableInfo event type {} dbEntity class {} addTableInfo ", eventId, dbEntity.getClass().getCanonicalName());
            tableInfo = addTableInfo(dbEntity.getClass());
//            return;
        }

        boolean needReturn;
        if (eventData.getEventExtData() != null && eventData.getEventExtData().requestId != 0) {
            needReturn = true;
        } else {
            needReturn = false;
        }

        Map<String, Object> params = null;
        if (eventData.getEventExtData() != null) {
            params = eventData.getEventExtData().params;
        }

        PrepareSQLAndParams prepareSQLAndParams = null;
        try {
            prepareSQLAndParams = sqlMaker.createSqlInfo(sqlKeyWord, dbEntity, tableInfo, params);
        } catch (Exception e) {
            e.printStackTrace();
            if (needReturn) {
                eventReturn(eventData.getEventExtData().fromConsumerId, 100, eventData.getEventExtData().requestId);
            }
        }

        if (prepareSQLAndParams == null) {
            return;
        }


        Object returnData = null;

        try {
            returnData = sqlExecutor.executeSql(prepareSQLAndParams, eventData, tableInfo);
        } catch (SQLException | IllegalAccessException | InstantiationException throwables) {
            throwables.printStackTrace();
            if (needReturn) {
                //eventId 1 : 报错
                eventReturn(eventData.getEventExtData().fromConsumerId, null, eventData.getEventExtData().requestId, DBErrorCode.EXCEPTION);
            }
            return;
        }


        int returnEventId = 0;
        if (returnData == null) {
            returnEventId = DBErrorCode.NULL;
        }

        if (needReturn) {
            eventReturn(eventData.getEventExtData().fromConsumerId, returnData, eventData.getEventExtData().requestId, returnEventId);
        }
    }

    @Override
    public boolean isDefaultConsumer() {
        return false;
    }

//    public void setDbConfig(DBConfig dbConfig) {
//        this.dbConfig = dbConfig;
//    }
}
