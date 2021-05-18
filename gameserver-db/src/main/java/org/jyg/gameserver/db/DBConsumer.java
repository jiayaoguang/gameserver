package org.jyg.gameserver.db;

import cn.hutool.core.collection.CollectionUtil;
import org.jyg.gameserver.core.consumer.BlockingQueueConsumer;
import org.jyg.gameserver.core.consumer.ConcurrentQueueConsumer;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.db.anno.DBTable;
import org.jyg.gameserver.db.anno.DBTableField;
import org.jyg.gameserver.db.anno.DBTableFieldIgnore;
import org.jyg.gameserver.db.serialize.DBFieldSerializer;
import org.jyg.gameserver.db.serialize.JSONDBFieldSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/11
 */
public class DBConsumer extends BlockingQueueConsumer {

    private Connection connection = null;

    private final SqlKeyWord sqlKeyWord;

    private final Map<Integer, SQLMaker> sqlTextMap = new HashMap<>(MAP_DEFAULT_SIZE,MAP_DEFAULT_LOADFACTOR);

    private final Map<Class<?>, TableInfo> tableInfoMap = new HashMap<>(MAP_DEFAULT_SIZE,MAP_DEFAULT_LOADFACTOR);
    /**
     * key class value : Serializer
     */
    private final Map<Class<?>,DBFieldSerializer<?>> dbFieldSerializerMap = new HashMap<>(MAP_DEFAULT_SIZE,MAP_DEFAULT_LOADFACTOR);

    public DBConsumer() {
        this.sqlKeyWord = new MySQLUpperKey();

        addSQLMaker(BDEventTypeConst.DB_EVENT_INSERT, new InsertSQLMaker());
        addSQLMaker(BDEventTypeConst.DB_EVENT_DELETE, new DeleteSQLMaker());
        addSQLMaker(BDEventTypeConst.DB_EVENT_UPDATE, new UpdateSQLMaker());
        addSQLMaker(BDEventTypeConst.DB_EVENT_SELECT, new SelectSQLMaker());

        addDBFieldSerializer(new JSONDBFieldSerializer());

    }

    public void addSQLMaker(int eventId, SQLMaker SQLMaker) {
        if (sqlTextMap.containsKey(eventId)) {
            throw new IllegalArgumentException(" addDBProcessor fail contains eventId " + eventId);
        }
        sqlTextMap.put(eventId, SQLMaker);
    }

    public void addTableInfo(Class<?> dbEntityClass) {
        if (tableInfoMap.containsKey(dbEntityClass)) {
            throw new IllegalArgumentException(" addTableInfo fail contains dbEntityClass " + dbEntityClass.getCanonicalName());
        }
        tableInfoMap.put(dbEntityClass, createTableInfo(dbEntityClass));
    }

    public void addDBFieldSerializer(DBFieldSerializer<?> dbFieldSerializer){
        if(dbFieldSerializerMap.containsKey(dbFieldSerializer.getSerializeClass())){
            throw new IllegalArgumentException(" addTableInfo fail contains dbFieldSerializerClass " + dbFieldSerializer.getSerializeClass().getCanonicalName());
        }
        dbFieldSerializerMap.put(dbFieldSerializer.getSerializeClass() , dbFieldSerializer);
    }

    /**
     * @param dbEntityClass dbEntityClass
     * @return TableInfo
     */
    private TableInfo createTableInfo(Class<?> dbEntityClass) {
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


        Field[] fields = dbEntityClass.getDeclaredFields();

        Map<String, TableFieldInfo> tableFieldInfoMap = new LinkedHashMap<>();

        for (Field field : fields) {

            if (field.getAnnotation(DBTableFieldIgnore.class) != null) {
                continue;
            }

            TableFieldInfo tableFieldInfo = createTableFieldInfo(dbEntityClass, field);
            tableFieldInfoMap.put(field.getName(), tableFieldInfo);

            field.setAccessible(true);
        }

        tableInfo.setFieldInfoMap(tableFieldInfoMap);

        return tableInfo;
    }

    private TableFieldInfo createTableFieldInfo(Class<?> dbEntityClass, Field dbEntityField) {
        TableFieldInfo tableFieldInfo = new TableFieldInfo();
        tableFieldInfo.setClassField(dbEntityField);

        DBTableField dbTableFieldAnno = dbEntityField.getAnnotation(DBTableField.class);
        if (dbTableFieldAnno != null) {
            tableFieldInfo.setTableFieldName(dbTableFieldAnno.fieldName());
            if(dbTableFieldAnno.fieldType() == TableFieldType.AUTO){
                TableFieldType tableFieldType = getTableFieldType(dbEntityField.getType());
                tableFieldInfo.setFieldType(tableFieldType);
            }else {
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
        } catch (NoSuchMethodException e) {

        }

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
        }else if(dbFieldSerializerMap.containsKey(fieldCLass)){
            return TableFieldType.VARCHAR;
        } else {
            throw new IllegalArgumentException(fieldCLass.getName() + " field type can not to sql type");
        }
    }

    private String getFieldGetMethodName(Field dbEntityField) {
        String fieldGetMethodName;

        char fieldFirstChar = dbEntityField.getName().charAt(0);

        if (fieldFirstChar >= 'a' && fieldFirstChar <= 'z') {

            if (dbEntityField.getName().startsWith("is")) {

            }

            if (dbEntityField.getName().length() <= 1) {
                char fieldFirstCharUpper = (char) (fieldFirstChar + 26);
                fieldGetMethodName = "get" + fieldFirstCharUpper;
            } else {
                fieldGetMethodName = "get" + dbEntityField.getName().indexOf(0) + dbEntityField.getName().substring(1);
            }
        } else {
            fieldGetMethodName = "get" + dbEntityField.getName();
        }

        return fieldGetMethodName;
    }

    @Override
    public void afterStart() {
        tryConnectIfCLose();
    }

    @Override
    public void doStop() {
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }


    @Override
    protected void processDefaultEvent(int eventId, Object dbEntity) {
        SQLMaker sqlMaker = sqlTextMap.get(eventId);

        if (sqlMaker == null) {
            Logs.DEFAULT_LOGGER.error(" unknow db event type {} ", eventId);
            return;
        }

        TableInfo tableInfo = tableInfoMap.get(dbEntity.getClass());
        if (tableInfo == null) {
            Logs.DEFAULT_LOGGER.error(" unknow tableInfo event type {} dbEntity class {} ", eventId, dbEntity.getClass().getCanonicalName());
            return;
        }

        PrepareSQLAndParams prepareSQLAndParams = sqlMaker.createSql(sqlKeyWord, dbEntity, tableInfo);

        if (prepareSQLAndParams == null) {
            return;
        }

        try {
            executeSql(prepareSQLAndParams);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    private void executeSql(PrepareSQLAndParams prepareSQLAndParams) throws SQLException {
        tryConnectIfCLose();

        Logs.DB.info(" prepareSQL : {} ", prepareSQLAndParams.prepareSQL);

        try(PreparedStatement preparedStatement = connection.prepareStatement(prepareSQLAndParams.prepareSQL);){

            if (!CollectionUtil.isEmpty(prepareSQLAndParams.params)) {
                for (int i = 0; i < prepareSQLAndParams.params.size(); i++) {
                    int parameterIndex = i + 1;
                    Object value = prepareSQLAndParams.params.get(i);

                    if (value instanceof Integer) {
                        preparedStatement.setInt(parameterIndex, (int) value);
                    } else if (value instanceof String) {
                        preparedStatement.setString(parameterIndex, (String) value);
                    } else if (value instanceof Long) {
                        preparedStatement.setLong(parameterIndex, (long) value);
                    } else if(dbFieldSerializerMap.containsKey(value.getClass())){
                        DBFieldSerializer<?> dbFieldSerializer = dbFieldSerializerMap.get(value.getClass());
                        String str = dbFieldSerializer.unserialize(value);
                        preparedStatement.setString(parameterIndex, str);
                    }else {
                        preparedStatement.setInt(parameterIndex, (int) value);
                    }
                }
            }

            preparedStatement.execute();
        }


    }


    private void tryConnectIfCLose() {
        if (connection == null) {
            try {
                connection = getConn("test");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return;
        }


        try {
            if (connection.isClosed()) {
                connection.close();
                connection = getConn("test");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


//        if(connection.isClosed()){
//            return;
//        }

    }


    private Connection getConn(String databaseName) throws SQLException {


        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + databaseName + "?useUnicode=true&characterEncoding=utf8&createDatabaseIfNotExist=true&serverTimezone=UTC"
                , "root", "123456"
        );
        return connection;

    }


}
