package org.jyg.gameserver.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/22
 */
public abstract class CachedSQLMaker implements SQLMaker {

    private final Map<String , String> prepareSqlMap = new HashMap<>();


    @Override
    public final PrepareSQLAndParams createSqlInfo(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception{

        String prepareSql = getPrepareSql(tableInfo);
        if (prepareSql == null) {
            prepareSql = createPrepareSql(sqlKeyWord, dbEntity, tableInfo, params);
            putPrepareSql(tableInfo, prepareSql);
        }

        List<Object> paramValues = getParamValues(sqlKeyWord , dbEntity , tableInfo , params);


        return new PrepareSQLAndParams(prepareSql ,paramValues , getExecuteType());
    }


    protected String getPrepareSql( TableInfo tableInfo) {
        return prepareSqlMap.get(tableInfo.getTableName());
    }

    protected void putPrepareSql(TableInfo tableInfo , String prepareSql){
        prepareSqlMap.put(tableInfo.getTableName() , prepareSql);
    }


    protected abstract String createPrepareSql(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception;

//    public abstract SqlExecuteType getExecuteType();

    protected abstract List<Object> getParamValues(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception;

}
