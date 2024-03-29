package org.jyg.gameserver.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/14
 */
public class DeleteSQLBuilder extends CachedSQLBuilder {

    @Override
    protected String createPrepareSql(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(sqlKeyWord.delete()).append(' ');
        sb.append(sqlKeyWord.from()).append(' ');
        sb.append('`').append(tableInfo.getTableName()).append('`').append(' ');
        sb.append(sqlKeyWord.where()).append(' ')
                .append('`').append(tableInfo.getPrimaryKey()).append('`')
                .append('=').append('?').append(';');
        return sb.toString();
    }

    @Override
    protected List<Object> getParamValues(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception {
        List<Object> valueParams = new ArrayList<>(1);
        TableFieldInfo primaryField = tableInfo.getFieldInfoLinkedMap().get(tableInfo.getPrimaryKey());

        Object value = primaryField.getFieldOperator().readObject(dbEntity);

        valueParams.add(value);
        return valueParams;
    }

    @Override
    public SqlExecuteType getExecuteType() {
        return SqlExecuteType.MODIFY;
    }
}
