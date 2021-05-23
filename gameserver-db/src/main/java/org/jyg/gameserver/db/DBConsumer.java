package org.jyg.gameserver.db;

import cn.hutool.core.collection.CollectionUtil;
import org.jyg.gameserver.core.consumer.BlockingQueueConsumer;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.db.anno.DBTable;
import org.jyg.gameserver.db.anno.DBTableField;
import org.jyg.gameserver.db.anno.DBTableFieldIgnore;
import org.jyg.gameserver.db.type.BooleanTypeHandler;
import org.jyg.gameserver.db.type.ByteTypeHandler;
import org.jyg.gameserver.db.type.CharacterTypeHandler;
import org.jyg.gameserver.db.type.DoubleTypeHandler;
import org.jyg.gameserver.db.type.FastJSONTypeHandler;
import org.jyg.gameserver.db.type.FloatTypeHandler;
import org.jyg.gameserver.db.type.IntegerTypeHandler;
import org.jyg.gameserver.db.type.LongTypeHandler;
import org.jyg.gameserver.db.type.ShortTypeHandler;
import org.jyg.gameserver.db.type.StringTypeHandler;
import org.jyg.gameserver.db.type.TypeHandler;

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

    private Connection connection = null;

    private final SqlKeyWord sqlKeyWord;

    private final Map<Integer, SQLMaker> sqlTextMap = new HashMap<>(MAP_DEFAULT_SIZE, MAP_DEFAULT_LOADFACTOR);

    private final Map<Class<?>, TableInfo> tableInfoMap = new HashMap<>(MAP_DEFAULT_SIZE, MAP_DEFAULT_LOADFACTOR);

    private DBConfig dbConfig;

    private final Map<Class<?>, TypeHandler<?>> typeHandlerMap = new HashMap<>();


    public DBConsumer() {
        this(null);

    }

    public DBConsumer(DBConfig dbConfig) {
        this.sqlKeyWord = new MySQLUpperKey();

        addSQLMaker(BDEventConst.INSERT, new InsertSQLMaker());
        addSQLMaker(BDEventConst.DELETE, new DeleteSQLMaker());
        addSQLMaker(BDEventConst.UPDATE, new UpdateSQLMaker());
        addSQLMaker(BDEventConst.SELECT, new SelectSQLMaker());

        addSQLMaker(BDEventConst.SELECT_BY_FIELD, new SelectByFieldSQLMaker());

        typeHandlerMap.put(int.class, new IntegerTypeHandler());
        typeHandlerMap.put(long.class, new LongTypeHandler());


        registerTypeHandler(new StringTypeHandler());

        registerTypeHandler(new LongTypeHandler());
        registerTypeHandler(new IntegerTypeHandler());
        registerTypeHandler(new ShortTypeHandler());
        registerTypeHandler(new ByteTypeHandler());
        registerTypeHandler(new DoubleTypeHandler());
        registerTypeHandler(new FloatTypeHandler());
        registerTypeHandler(new CharacterTypeHandler());
        registerTypeHandler(new BooleanTypeHandler());

        registerTypeHandler(new FastJSONTypeHandler());

        this.dbConfig = dbConfig;

    }

    public void registerTypeHandler(TypeHandler<?> typeHandler) {

        if (typeHandlerMap.containsKey(typeHandler.getBindClassType())) {
            throw new RuntimeException("containsKey");
        }

        typeHandlerMap.put(typeHandler.getBindClassType(), typeHandler);
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
    private TableInfo createTableInfo(Class<?> dbEntityClass) throws NoSuchFieldException , NoSuchMethodException {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setDbEntityClass(dbEntityClass);

        DBTable dbTableAnno = dbEntityClass.getAnnotation(DBTable.class);
        if (dbTableAnno != null) {
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
            if(AllUtil.isStatic(field)){
                continue;
            }

            if(tableFieldInfoMap.containsKey(field.getName())){
                throw new IllegalArgumentException("class " + dbEntityClass.getCanonicalName() +" duplicate field "+ field.getName());
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


        Method fiedGetMethod = dbEntityClass.getMethod(fieldGetMethodName);
        tableFieldInfo.setFiedGetMethod(fiedGetMethod);

        return tableFieldInfo;
    }


    private TableFieldType getTableFieldType(Class<?> fieldCLass) {
        if (fieldCLass == int.class || fieldCLass == Integer.class) {
            return TableFieldType.INTEGER;
        } else if (fieldCLass == String.class) {
            return TableFieldType.VARCHAR;
        } else if (fieldCLass == long.class || fieldCLass == Long.class) {
            return TableFieldType.BIGINT;
        } else if (fieldCLass == short.class || fieldCLass == Short.class) {
            return TableFieldType.SMALLINT;
        } else if (fieldCLass == float.class || fieldCLass == Float.class) {
            return TableFieldType.FLOAT;
        } else if (fieldCLass == double.class || fieldCLass == Double.class) {
            return TableFieldType.DOUBLE;
        } else if (fieldCLass == boolean.class || fieldCLass == Boolean.class
                || fieldCLass == byte.class || fieldCLass == Byte.class) {
            return TableFieldType.TINYINT;
        } else if (typeHandlerMap.containsKey(fieldCLass)) {
            return TableFieldType.VARCHAR;
        } else {

            TypeHandler<?> typeHandler = typeHandlerMap.get(fieldCLass);
            if (typeHandler != null) {
                return typeHandler.getTableFieldType();
            }

            throw new IllegalArgumentException(fieldCLass.getName() + " field type can not to sql type");
        }
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
        tryConnectIfCLose();
    }

    @Override
    public void doStop() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
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


        List<Object> returnList = null;


        try {
            returnList = executeSql(prepareSQLAndParams, eventData, tableInfo);
        } catch (SQLException | IllegalAccessException | InstantiationException throwables) {
            throwables.printStackTrace();
            if (needReturn) {
                //eventId 1 : 报错
                eventReturn(eventData.getEventExtData().fromConsumerId, null, eventData.getEventExtData().requestId, DBErrorCode.EXCEPTION);
            }
            return;
        }


        Object returnData = null;

        if (prepareSQLAndParams.sqlExecuteType == SqlExecuteType.QUERY_ONE) {
            if (returnList != null && returnList.size() >= 1) {
                returnData = returnList.get(0);
            }
        } else {
            returnData = returnList;
        }

        int returnEventId = 0;
        if (returnData == null) {
            returnEventId = DBErrorCode.NULL;
        }

        if (needReturn) {
            eventReturn(eventData.getEventExtData().fromConsumerId, returnData, eventData.getEventExtData().requestId, returnEventId);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Object> executeSql(PrepareSQLAndParams prepareSQLAndParams, EventData eventData, TableInfo tableInfo) throws SQLException, IllegalAccessException, InstantiationException {
        tryConnectIfCLose();

        Logs.DB.info(" prepareSQL : {} ", prepareSQLAndParams.prepareSQL);

        try (PreparedStatement preparedStatement = connection.prepareStatement(prepareSQLAndParams.prepareSQL);) {

            if (!CollectionUtil.isEmpty(prepareSQLAndParams.paramValues)) {
                for (int i = 0; i < prepareSQLAndParams.paramValues.size(); i++) {
                    int parameterIndex = i + 1;
                    Object value = prepareSQLAndParams.paramValues.get(i);


                    TypeHandler typeHandler = typeHandlerMap.get(value.getClass());
                    if (typeHandler != null) {
                        typeHandler.setParameter(preparedStatement, parameterIndex, value);
                    } else {
                        preparedStatement.setObject(parameterIndex, value);
                    }
                }
            }

            switch (prepareSQLAndParams.sqlExecuteType) {
                case QUERY_ONE:
                case QUERY_MANY: {
                    List<Object> returnList = new ArrayList<>();
                    try (ResultSet resultSet = preparedStatement.executeQuery();) {

                        while (!resultSet.isClosed() && resultSet.next()) {


                            Object returnObj = eventData.getData().getClass().newInstance();

                            for (TableFieldInfo tableFieldInfo : tableInfo.getFieldInfoLinkedMap().values()) {

                                TypeHandler typeHandler = typeHandlerMap.get(tableFieldInfo.getClassField().getType());
                                if (typeHandler == null) {
                                    Logs.DB.error("typeHandler == null class field {}", tableFieldInfo.getClassField().getType());
                                    continue;
                                }

                                Object value = typeHandler.getNullableResult(resultSet, tableFieldInfo.getTableFieldName());

                                tableFieldInfo.getClassField().set(returnObj, value);
                            }

                            returnList.add(returnObj);
                        }
                    }
                    return returnList;
                }
                case MODIFY:
                default:
                    preparedStatement.execute();
                    break;
            }

            return null;


        }


    }


    private void tryConnectIfCLose() {
        if (connection == null) {
            try {
                connection = getConn();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return;
        }


        boolean isClose = false;

        try {
            isClose = connection.isClosed();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            isClose = true;
        }


        if (isClose) {
            closeQuiet();
            try {
                connection = getConn();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }


//        if(connection.isClosed()){
//            return;
//        }

    }

    private void closeQuiet() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private Connection getConn() throws SQLException {

        Connection connection = DriverManager.getConnection(dbConfig.getJdbcUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        return connection;

    }

    @Override
    public boolean isDefaultConsumer(){
        return false;
    }

    public void setDbConfig(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
}
