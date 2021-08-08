package org.jyg.gameserver.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/14
 */
public class SelectSQLMaker extends CachedSQLMaker {

    @Override
    protected String createPrepareSql(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(sqlKeyWord.select()).append(' ');
        sb.append('*').append(' ');
        sb.append(sqlKeyWord.from()).append(' ');
        sb.append('`').append(tableInfo.getTableName()).append('`').append(' ');
        sb.append(sqlKeyWord.where()).append(' ');
        sb.append('`').append(tableInfo.getPrimaryKey()).append('`').append('=');
        sb.append('?').append(';');
        String selectSql = sb.toString();
        return selectSql;
    }

    @Override
    protected List<Object> getParamValues(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception {

        List<Object> valueParams = new ArrayList<>(1);
        TableFieldInfo primaryField = tableInfo.getFieldInfoLinkedMap().get(tableInfo.getPrimaryKey());
//        Field primaryClassField = primaryField.getClassField();

        Object value = primaryField.getFieldOperator().readObject(dbEntity);

        valueParams.add(value);

        return valueParams;
    }


    @Override
    public SqlExecuteType getExecuteType() {
        return SqlExecuteType.QUERY_ONE;
    }

}
