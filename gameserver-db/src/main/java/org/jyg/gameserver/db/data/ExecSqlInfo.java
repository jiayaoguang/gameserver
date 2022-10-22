package org.jyg.gameserver.db.data;

import org.jyg.gameserver.db.SqlExecuteType;

import java.util.List;

/**
 * create by jiayaoguang on 2022/10/22
 */
public class ExecSqlInfo {

    private final String prepareSql;

    private final List<Object> params;

    private final Class<?> dbEntityClazz;

    private final SqlExecuteType sqlExecuteType;


    public ExecSqlInfo(String prepareSql, List<Object> params, Class<?> dbEntityClazz, SqlExecuteType sqlExecuteType) {
        this.prepareSql = prepareSql;
        this.params = params;
        this.dbEntityClazz = dbEntityClazz;
        this.sqlExecuteType = sqlExecuteType;
    }


    public String getPrepareSql() {
        return prepareSql;
    }

    public List<Object> getParams() {
        return params;
    }

    public Class<?> getDbEntityClazz() {
        return dbEntityClazz;
    }

    public SqlExecuteType getSqlExecuteType() {
        return sqlExecuteType;
    }
}
