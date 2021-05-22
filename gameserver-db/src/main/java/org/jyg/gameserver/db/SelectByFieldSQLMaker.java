package org.jyg.gameserver.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/16
 */
public class SelectByFieldSQLMaker implements SQLMaker {
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


        StringBuilder sb = new StringBuilder();

        sb.append(sqlKeyWord.select()).append(' ');
        sb.append('*').append(' ');
        sb.append(sqlKeyWord.from()).append(' ');
        sb.append(tableInfo.getTableName()).append(' ');
        sb.append(sqlKeyWord.where()).append(' ');
        sb.append(selectKey).append('=');
        sb.append('?').append(';');


        String selectSql = sb.toString();

        List<Object> values = new ArrayList<>(1);
//        TableFieldInfo primaryField = tableInfo.getFieldInfoMap().get(tableInfo.getPrimaryKey());
        Field selectField = selectTableField.getClassField();

        Object value = selectField.get(dbEntity);

        values.add(value);

        return new PrepareSQLAndParams(selectSql, values, SqlExecuteType.QUERY_MANY);
    }


    @Override
    public SqlExecuteType getExecuteType() {
        return SqlExecuteType.QUERY_MANY;
    }

}
