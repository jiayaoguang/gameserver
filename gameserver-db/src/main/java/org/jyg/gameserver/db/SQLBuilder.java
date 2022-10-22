package org.jyg.gameserver.db;

import java.sql.Connection;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/14
 */
public interface SQLBuilder {
    PrepareSQLAndParams createSqlInfo(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo , Map<String,Object> params) throws Exception;

    SqlExecuteType getExecuteType();
}
