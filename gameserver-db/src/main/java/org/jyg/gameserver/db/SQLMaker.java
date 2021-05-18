package org.jyg.gameserver.db;

import java.sql.Connection;

/**
 * create by jiayaoguang at 2021/5/14
 */
public interface SQLMaker {
    PrepareSQLAndParams createSql(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo);
}
