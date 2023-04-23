package org.jyg.gameserver.db;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jyg.gameserver.core.consumer.MpscQueueGameConsumer;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.ConsumerDefaultEvent;
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

    private final DBConfig dbConfig;

    private final SimpleDataSource dataSource;
    private final TypeHandlerRegistry typeHandlerRegistry;

    private final SqlExecutor sqlExecutor;

    private final DBTableManager dbTableManager;

    private final boolean needCloneDBEntity;


//    public DBConsumer() {
//        this(null);
//
//    }

    public DBGameConsumer(DBConfig dbConfig) {

        this(dbConfig , true);
    }

    protected DBGameConsumer(DBConfig dbConfig , boolean needCloneDBEntity) {
        this.dbConfig = dbConfig;
        this.sqlKeyWord = new MySQLUpperKey();
        this.dataSource = new SimpleDataSource(dbConfig);
        this.typeHandlerRegistry = new TypeHandlerRegistry();
        this.sqlExecutor = new SqlExecutor(dataSource , typeHandlerRegistry);
        this.dbTableManager = new DBTableManager(typeHandlerRegistry);
        this.needCloneDBEntity = needCloneDBEntity;
        init();
    }


    private void init() {
        addSQLBuilder(BDEventConst.INSERT, new InsertSQLBuilder());
        addSQLBuilder(BDEventConst.DELETE, new DeleteSQLBuilder());
        addSQLBuilder(BDEventConst.UPDATE, new UpdateSQLBuilder());
        addSQLBuilder(BDEventConst.SELECT, new SelectSQLBuilder());

        addSQLBuilder(BDEventConst.SELECT_BY_FIELD, new SelectByFieldSQLBuilder());
        addSQLBuilder(BDEventConst.UPDATE_FIELD, new UpdateFieldSQLBuilder());

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


    @Override
    public void publicEvent(EventData eventData) {
//        if(!(data instanceof BaseDBEntity)){
//            throw new IllegalArgumentException("db entity must extend BaseDBEntity");
//        }
        if(needCloneDBEntity){
            if(eventData.getEvent() instanceof ConsumerDefaultEvent){
                ConsumerDefaultEvent consumerDefaultEvent = (ConsumerDefaultEvent) eventData.getEvent();
                if(consumerDefaultEvent.getData() instanceof BaseDBEntity){
                    consumerDefaultEvent.setData(((BaseDBEntity) consumerDefaultEvent.getData()).clone());
                }
            }
        }

        super.publicEvent(eventData);
    }



    public void registerTypeHandler(Class<?> clazz , TypeHandler<?> typeHandler) {
        typeHandlerRegistry.registerTypeHandler(clazz,typeHandler);
    }

    public TypeHandler<?> getTypeHandler(Class<?> clazz) {
        return typeHandlerRegistry.getTypeHandler(clazz);
    }


    public void addSQLBuilder(int eventId, SQLBuilder SQLBuilder) {
        if (sqlTextMap.containsKey(eventId)) {
            throw new IllegalArgumentException(" addDBProcessor fail contains eventId " + eventId);
        }
        sqlTextMap.put(eventId, SQLBuilder);
    }

    public TableInfo tryAddTableInfo(Class<?> dbEntityClass) {
        return dbTableManager.tryAddTableInfo(dbEntityClass);
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
    public void processDefaultEvent(ConsumerDefaultEvent event) {

        int eventId = event.getEventId();

        if(event.getData() == null){
            Logs.DB.error("DB eventData.getData() == null");
            return;
        }

        Class<?> dbEntityClazz = event.getData().getClass();


        if(event.getData() instanceof ExecSqlInfo){
            ExecSqlInfo execSqlInfo = (ExecSqlInfo) event.getData();
            dbEntityClazz = execSqlInfo.getDbEntityClazz();
        }

        TableInfo tableInfo = null;
        if(dbEntityClazz != null){
            tableInfo = dbTableManager.getTableInfo(dbEntityClazz);
            if (tableInfo == null) {
                Logs.DEFAULT_LOGGER.info(" unknow tableInfo event type {} dbEntity class {} addTableInfo ", eventId, dbEntityClazz.getCanonicalName());
                tableInfo = tryAddTableInfo(dbEntityClazz);
//            return;
            }
        }


        boolean needReturn;
        if ( event.getRequestId() != 0) {
            needReturn = true;
        } else {
            needReturn = false;
        }

        Map<String, Object> params = event.getParams();


        PrepareSQLAndParams prepareSQLAndParams = null;

        if(event.getData() instanceof ExecSqlInfo){
            ExecSqlInfo execSqlInfo = (ExecSqlInfo) event.getData();
            prepareSQLAndParams = new PrepareSQLAndParams(execSqlInfo.getPrepareSql() , execSqlInfo.getParams() , execSqlInfo.getSqlExecuteType());
        }else {

            SQLBuilder sqlBuilder = sqlTextMap.get(eventId);
            if (sqlBuilder == null) {
                Logs.DEFAULT_LOGGER.error(" unknow db event type {} ", eventId);
                return;
            }
            try {
                prepareSQLAndParams = sqlBuilder.createSqlInfo(sqlKeyWord, event.getData(), tableInfo, params);
            } catch (Exception e) {
                e.printStackTrace();
                if (needReturn) {
                    eventReturn(event.getFromConsumerId(), 100, event.getRequestId());
                }
                return;
            }
        }


        if (prepareSQLAndParams == null) {
            return;
        }


        Object returnData = null;

        try {
            if(tableInfo != null){
                returnData = sqlExecutor.executeSql(prepareSQLAndParams, dbEntityClazz, tableInfo);
            }else {
                returnData = sqlExecutor.executeSql(prepareSQLAndParams);
            }


            logSql(prepareSQLAndParams.prepareSQL , prepareSQLAndParams.paramValues);

        } catch (SQLException | IllegalAccessException | InstantiationException exception) {
            String stackTraceMsg = ExceptionUtils.getStackTrace(exception);
            Logs.DB.error("exec sql : {} make exception : {}" , prepareSQLAndParams.prepareSQL , stackTraceMsg);

            if (needReturn) {
                //eventId 1 : 报错
                eventReturn(event.getFromConsumerId(), stackTraceMsg, event.getRequestId(), DBErrorCode.EXCEPTION);
            }
            return;
        }


        int returnEventId = 0;
        if (returnData == null) {
            returnEventId = DBErrorCode.NULL;
        }

        if (needReturn) {
            eventReturn(event.getFromConsumerId(), returnData, event.getRequestId(), returnEventId);
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
    public boolean isMainConsumer() {
        return false;
    }

//    public void setDbConfig(DBConfig dbConfig) {
//        this.dbConfig = dbConfig;
//    }


    public DBTableManager getDbTableManager() {
        return dbTableManager;
    }



}
