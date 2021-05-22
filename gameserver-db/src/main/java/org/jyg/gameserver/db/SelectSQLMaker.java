package org.jyg.gameserver.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/14
 */
public class SelectSQLMaker implements SQLMaker {
    @Override
    public PrepareSQLAndParams createSqlInfo(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String,Object> params) throws IllegalAccessException {


        StringBuilder sb = new StringBuilder();

        sb.append(sqlKeyWord.select()).append(' ');
        sb.append('*').append(' ');
        sb.append(sqlKeyWord.from()).append(' ');
        sb.append(tableInfo.getTableName()).append(' ');
        sb.append(sqlKeyWord.where()).append(' ');
        sb.append(tableInfo.getPrimaryKey()).append('=');
        sb.append('?').append(';');


        String selectSql = sb.toString();

        List<Object> valueParams = new ArrayList<>(1);
        TableFieldInfo primaryField = tableInfo.getFieldInfoLinkedMap().get(tableInfo.getPrimaryKey());
        Field primaryClassField = primaryField.getClassField();

        Object value = primaryClassField.get(dbEntity);

        valueParams.add(value);

        return new PrepareSQLAndParams(selectSql, valueParams, SqlExecuteType.QUERY_ONE);
    }


    @Override
    public SqlExecuteType getExecuteType() {
        return SqlExecuteType.QUERY_ONE;
    }

}
