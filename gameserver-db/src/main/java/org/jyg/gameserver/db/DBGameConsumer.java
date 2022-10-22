package org.jyg.gameserver.db;

import org.jyg.gameserver.core.consumer.MpscQueueGameConsumer;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.db.data.ExecSqlInfo;
import org.jyg.gameserver.db.type.TypeHandler;
import org.jyg.gameserver.db.type.TypeHandlerRegistry;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/16
 */
public class DBGameConsumer extends MpscQueueGameConsumer {

    private final SqlKeyWord sqlKeyWord;

    private final Map<Integer, SQLBuilder> sqlTextMap = new HashMap<>(MAP_DEFAULT_SIZE, MAP_DEFAULT_LOADFACTOR);

    private final Map<Class<?>, TableInfo> tableInfoMap = new HashMap<>(MAP_DEFAULT_SIZE, MAP_DEFAULT_LOADFACTOR);

    private final DBConfig dbConfig;

    private final SimpleDataSource dataSource;
    private final TypeHandlerRegistry typeHandlerRegistry;

    private final SqlExecutor sqlExecutor;

    private final DBTableManager dbTableManager;


//    public DBConsumer() {
//        this(null);
//
//    }

    public DBGameConsumer(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
        this.sqlKeyWord = new MySQLUpperKey();
        this.dataSource = new SimpleDataSource(dbConfig);
        this.typeHandlerRegistry = new TypeHandlerRegistry();
        this.sqlExecutor = new SqlExecutor(dataSource , typeHandlerRegistry);
        this.dbTableManager = new DBTableManager(typeHandlerRegistry);
        init();

    }

    private void init() {
        addSQLMaker(BDEventConst.INSERT, new InsertSQLBuilder());
        addSQLMaker(BDEventConst.DELETE, new DeleteSQLBuilder());
        addSQLMaker(BDEventConst.UPDATE, new UpdateSQLBuilder());
        addSQLMaker(BDEventConst.SELECT, new SelectSQLBuilder());

        addSQLMaker(BDEventConst.SELECT_BY_FIELD, new SelectByFieldSQLBuilder());


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


    public void addSQLMaker(int eventId, SQLBuilder SQLBuilder) {
        if (sqlTextMap.containsKey(eventId)) {
            throw new IllegalArgumentException(" addDBProcessor fail contains eventId " + eventId);
        }
        sqlTextMap.put(eventId, SQLBuilder);
    }

    public TableInfo addTableInfo(Class<?> dbEntityClass) {
        return dbTableManager.addTableInfo(dbEntityClass);
    }

    public TableInfo getTableInfo(Class<?> dbEntityClass) {
        return dbTableManager.getTableInfo(dbEntityClass);
    }





    @Override
    public void beforeStart() {
        super.beforeStart();
        sqlExecutor.tryConnectIfClose();
    }

    @Override
    public void doStop() {
        super.doStop();
        sqlExecutor.closeQuiet();
    }


    @Override
    protected void processDefaultEvent(int eventId, EventData eventData) {

        if(eventData.getData() == null){
            Logs.DB.error("DB eventData.getData() == null");
            return;
        }

        Class<?> dbEntityClazz = eventData.getData().getClass();


        if(eventData.getData() instanceof ExecSqlInfo){
            ExecSqlInfo execSqlInfo = (ExecSqlInfo) eventData.getData();
            dbEntityClazz = execSqlInfo.getDbEntityClazz();
        }

        TableInfo tableInfo = dbTableManager.getTableInfo(dbEntityClazz);
        if (tableInfo == null) {
            Logs.DEFAULT_LOGGER.info(" unknow tableInfo event type {} dbEntity class {} addTableInfo ", eventId, dbEntityClazz.getCanonicalName());
            tableInfo = addTableInfo(dbEntityClazz);
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

        if(eventData.getData() instanceof ExecSqlInfo){
            ExecSqlInfo execSqlInfo = (ExecSqlInfo) eventData.getData();
            prepareSQLAndParams = new PrepareSQLAndParams(execSqlInfo.getPrepareSql() , execSqlInfo.getParams() , execSqlInfo.getSqlExecuteType());
        }else {

            SQLBuilder sqlBuilder = sqlTextMap.get(eventId);
            if (sqlBuilder == null) {
                Logs.DEFAULT_LOGGER.error(" unknow db event type {} ", eventId);
                return;
            }
            try {
                prepareSQLAndParams = sqlBuilder.createSqlInfo(sqlKeyWord, eventData.getData(), tableInfo, params);
            } catch (Exception e) {
                e.printStackTrace();
                if (needReturn) {
                    eventReturn(eventData.getEventExtData().fromConsumerId, 100, eventData.getEventExtData().requestId);
                }
                return;
            }
        }


        if (prepareSQLAndParams == null) {
            return;
        }


        Object returnData = null;

        try {
            returnData = sqlExecutor.executeSql(prepareSQLAndParams, dbEntityClazz, tableInfo);

            logSql(prepareSQLAndParams.prepareSQL , prepareSQLAndParams.paramValues);

        } catch (SQLException | IllegalAccessException | InstantiationException exception) {
            Logs.DB.info("exec sql : {} make exception" , prepareSQLAndParams.prepareSQL);
            exception.printStackTrace();
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




    private void logSql(String prepareSQL , List<Object> paramValues){
//        if(dbConfig.getPrintSqlLevel() == 0){
//            return;
//        }

        if(dbConfig.getPrintSqlLevel() == 1){
            Logs.DB.info("exec sql : {}" , prepareSQL);
            return;
        }
        if(dbConfig.getPrintSqlLevel() == 2){

            StringBuilder sb = new StringBuilder(prepareSQL.length() + paramValues.size() * 10);
            sb.append(prepareSQL);

            for(Object param : paramValues ){
                TypeHandler typeHandler =  typeHandlerRegistry.getTypeHandler(param.getClass());
                String strValue = typeHandler.typeToString(param);
                sb.append(',').append(strValue);
            }

            Logs.DB.info("exec sql : {}" , sb.toString());
            return;
        }
    }

    @Override
    public boolean isDefaultConsumer() {
        return false;
    }

//    public void setDbConfig(DBConfig dbConfig) {
//        this.dbConfig = dbConfig;
//    }


    public DBTableManager getDbTableManager() {
        return dbTableManager;
    }
}
