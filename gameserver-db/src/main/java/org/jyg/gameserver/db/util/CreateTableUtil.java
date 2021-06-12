package org.jyg.gameserver.db.util;

import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.ConfigUtil;
import org.jyg.gameserver.db.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * create by jiayaoguang at 2021/5/29
 */
public class CreateTableUtil {


    private CreateTableUtil() {
    }

    public static void createTable(Class<?> dbClass) throws Exception {
        createTable( ConfigUtil.properties2Object("jyg", DBConfig.class) , dbClass);
    }

    public static void createTable(DBConfig dbConfig ,Class<?> dbClass) throws Exception {

//        DBConfig dbConfig = ConfigUtil.properties2Object("jyg", DBConfig.class);

        DBTableManager dbTableManager = new DBTableManager(null);

        TableInfo tableInfo = dbTableManager.addTableInfo(dbClass);

        StringBuilder sqlSB = new StringBuilder("CREATE TABLE " + tableInfo.getTableName());
        sqlSB.append("(");

        for (TableFieldInfo tableFieldInfo : tableInfo.getFieldInfoLinkedMap().values()) {
            TableFieldType tableFieldType = tableFieldInfo.getFieldType();
            if (tableFieldType == TableFieldType.AUTO) {
                tableFieldType = dbTableManager.getTableFieldType(tableFieldInfo.getClassField().getType());
            }
            sqlSB.append(tableFieldInfo.getTableFieldName()).append(" ").append(tableFieldType.name)
                    .append("(").append(tableFieldInfo.getLength()).append(")").append(" ");

            if (tableFieldInfo.getDbTableFieldAnno() != null && StringUtils.isNotEmpty(tableFieldInfo.getDbTableFieldAnno().comment())) {
                sqlSB.append("COMMENT").append(" '").append(tableFieldInfo.getDbTableFieldAnno().comment()).append("'");
            }
            sqlSB.append(",");
        }
        sqlSB.append(" PRIMARY KEY (").append(tableInfo.getPrimaryKey()).append(")");

        sqlSB.append(")");
        if (tableInfo.getDbTableAnno() != null && StringUtils.isNotEmpty(tableInfo.getDbTableAnno().comment())) {
            sqlSB.append(" COMMENT = '").append(tableInfo.getDbTableAnno().comment()).append("'");
        }
        sqlSB.append(" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");


        for (TableFieldInfo tableFieldInfo : tableInfo.getFieldInfoLinkedMap().values()) {
            if (tableFieldInfo.getFieldIndexType() == FieldIndexType.NONE) {
                continue;
            }
            String indexSql = getIndexSql(tableInfo.getTableName(), tableFieldInfo.getTableFieldName(), tableFieldInfo.getFieldIndexType());
            if (StringUtils.isNotEmpty(indexSql)) {
                sqlSB.append(indexSql).append("\n");
            }
        }


        String sql = sqlSB.toString();

        AllUtil.println(sql);

        Connection connection = getConn(dbConfig);

        Statement statement = connection.createStatement();

        statement.execute(sql);
        statement.close();
        connection.close();

    }


    private static String getIndexSql(String tableName, String fieldName, FieldIndexType fieldIndexType) {


        if (fieldIndexType == FieldIndexType.NONE) {
            return null;
        }

        if (fieldIndexType == FieldIndexType.UNIQUE) {
            String uniqueSql = "ALTER TABLE `" + tableName + "` ADD UNIQUE ( \n" +
                    "`" + fieldName + "` \n" +
                    ") ";
            return uniqueSql;
        }

        if (fieldIndexType == FieldIndexType.INDEX) {
            String indexSql = "ALTER TABLE `" + tableName + "` ADD UNIQUE ( \n" +
                    "`" + fieldName + "` \n" +
                    ") ";
            return indexSql;
        }

        return null;


    }


    private static Connection getConn(DBConfig dbConfig) throws SQLException {

        Connection connection = DriverManager.getConnection(dbConfig.getJdbcUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        return connection;

    }

}
