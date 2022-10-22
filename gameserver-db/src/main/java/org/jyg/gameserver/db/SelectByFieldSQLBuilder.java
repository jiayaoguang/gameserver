package org.jyg.gameserver.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/16
 */
public class SelectByFieldSQLBuilder implements SQLBuilder {

    private final Map<String,String> prepareSqlCache = new HashMap<>();

    @Override
    public PrepareSQLAndParams createSqlInfo(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws IllegalAccessException {

        if(params == null){
            return null;
        }

        Object obj = params.get("field");
        if (obj == null) {
            return null;
        }

        if (!(obj instanceof String)) {
            return null;
        }
        String selectKey = (String) obj;

        TableFieldInfo selectTableField = tableInfo.getFieldInfoLinkedMap().get(selectKey);

        String selectSql = getPrepareSql(sqlKeyWord, tableInfo, selectKey);

        List<Object> values = new ArrayList<>(1);
//        TableFieldInfo primaryField = tableInfo.getFieldInfoMap().get(tableInfo.getPrimaryKey());
//        Field selectField = selectTableField.getClassField();

        Object value = selectTableField.getFieldOperator().readObject(dbEntity);

        values.add(value);

        return new PrepareSQLAndParams(selectSql, values, SqlExecuteType.QUERY_MANY);
    }

    private String getPrepareSql(SqlKeyWord sqlKeyWord,  TableInfo tableInfo , String selectKey){

        String cacheKey = tableInfo.getTableName() + '_' + selectKey;
        String sql = prepareSqlCache.get(cacheKey);
        if(sql != null){
            return sql;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(sqlKeyWord.select()).append(' ');
        sb.append('*').append(' ');
        sb.append(sqlKeyWord.from()).append(' ');
        sb.append('`').append(tableInfo.getTableName()).append('`').append(' ');
        sb.append(sqlKeyWord.where()).append(' ');
        sb.append('`').append(selectKey).append('`').append('=');
        sb.append('?').append(';');

        sql = sb.toString();

        prepareSqlCache.put(cacheKey , sql);

        return sql;
    }


    @Override
    public SqlExecuteType getExecuteType() {
        return SqlExecuteType.QUERY_MANY;
    }

}
