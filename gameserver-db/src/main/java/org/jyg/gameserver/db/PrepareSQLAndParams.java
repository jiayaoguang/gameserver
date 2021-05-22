package org.jyg.gameserver.db;

import java.util.List;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class PrepareSQLAndParams {

    public final String prepareSQL;
    public final List<Object> paramValues;

    public final SqlExecuteType sqlExecuteType;

    public PrepareSQLAndParams(String prepareSQL, List<Object> paramValues, SqlExecuteType sqlExecuteType) {
        this.prepareSQL = prepareSQL;
        this.paramValues = paramValues;
        this.sqlExecuteType = sqlExecuteType;
    }

    public PrepareSQLAndParams(String prepareSQL, List<Object> paramValues) {
        this.prepareSQL = prepareSQL;
        this.paramValues = paramValues;
        this.sqlExecuteType = SqlExecuteType.MODIFY;
    }
}
