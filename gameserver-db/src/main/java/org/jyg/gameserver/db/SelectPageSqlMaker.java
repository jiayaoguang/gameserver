package org.jyg.gameserver.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/7/3
 */
public class SelectPageSqlMaker extends CachedSQLMaker {


    @Override
    protected String createPrepareSql(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(sqlKeyWord.select()).append(' ');
        sb.append('*').append(' ');
        sb.append(sqlKeyWord.from()).append(' ');
        sb.append('`').append(tableInfo.getTableName()).append('`');

//        sb.append(' ').append(sqlKeyWord.where()).append(" id > ").append(params.get("fromId")).append(' ') ;
        sb.append(' ').append(sqlKeyWord.limit()).append(' ').append('?').append(", ").append('?');

        sb.append(';');

        return sb.toString();
    }

    @Override
    protected List<Object> getParamValues(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception {

        Object fromParam = params.getOrDefault("from", 0);
        Object pageSizeParam = params.getOrDefault("pageSize", 20000);
        if (fromParam == null) {
            throw new RuntimeException("fromParam == null");
        }
        if (!(fromParam instanceof Number)) {
            throw new RuntimeException("!(fromParam instanceof Number)");
        }

        if (pageSizeParam == null) {
            throw new RuntimeException("fromParam == null");
        }
        if (!(pageSizeParam instanceof Number)) {
            throw new RuntimeException("!(pageSizeParam instanceof Number");
        }

//        Number fromNum = (Number) fromParam;

        List<Object> paramValues = new ArrayList<>(2);

        paramValues.add(fromParam);
        paramValues.add(pageSizeParam);
        return paramValues;
    }


    @Override
    public SqlExecuteType getExecuteType() {
        return SqlExecuteType.QUERY_MANY;
    }


}
