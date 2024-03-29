package org.jyg.gameserver.db.util;

import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.ConfigUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.db.*;
import org.jyg.gameserver.db.type.TypeHandlerRegistry;

import java.sql.*;

/**
 * create by jiayaoguang at 2021/5/29
 */
public class CreateTableUtil {


    private CreateTableUtil() {
    }

    
    public static void createTable(Class<? extends BaseDBEntity> dbClass) {
        createTable( ConfigUtil.properties2Object(GameContext.DEFAULT_CONFIG_FILE_NAME, DBConfig.class) , dbClass);
    }

    public static void createTable(DBConfig dbConfig ,Class<? extends BaseDBEntity> dbClass) {

//        DBConfig dbConfig = ConfigUtil.properties2Object("jyg", DBConfig.class);

        String sql = getCreateTableSql(dbClass);

        try(Connection connection = getConn(dbConfig);
            Statement statement = connection.createStatement();
            ){



            statement.execute(sql);

        }catch (Exception e){
            Logs.DEFAULT_LOGGER.error("execute create table fail , sql {}",sql);
            e.printStackTrace();
        }




    }

    public static String getCreateTableSql(Class<? extends BaseDBEntity> dbClass) {
        DBTableManager dbTableManager = new DBTableManager(new TypeHandlerRegistry());

        TableInfo tableInfo = dbTableManager.tryAddTableInfo(dbClass);

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
        String collate = "utf8mb4_general_ci";
        if(tableInfo.getDbTableAnno() != null && StringUtils.isNotEmpty(tableInfo.getDbTableAnno().collate())){
            collate = tableInfo.getDbTableAnno().collate();
        }

        sqlSB.append(" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=")
                .append(collate)
                .append(";");


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

        return sql;
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
    
    

    public static boolean isTableExist(Class<? extends BaseDBEntity> dbClass) throws Exception {
        return isTableExist( ConfigUtil.properties2Object(GameContext.DEFAULT_CONFIG_FILE_NAME, DBConfig.class) , dbClass);
    }


    public static boolean isTableExist(DBConfig dbConfig ,Class<? extends BaseDBEntity> dbClass ) throws SQLException {
        String sql = "SHOW TABLES LIKE ?";


        DBTableManager dbTableManager = new DBTableManager(new TypeHandlerRegistry());
        

        TableInfo tableInfo = dbTableManager.tryAddTableInfo(dbClass);
        AllUtil.println("table : " +tableInfo.getTableName());

        try(Connection connection = getConn(dbConfig);
            PreparedStatement ps = connection.prepareStatement(sql);
        ){

            ps.setString(1, tableInfo.getTableName());
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                return true;
            }else {

                return false;
            }

        }


    }





    private static Connection getConn(DBConfig dbConfig) throws SQLException {

        Connection connection = DriverManager.getConnection(dbConfig.getJdbcUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        return connection;

    }

    
    
    public static void createOrAlterTable(Class<? extends BaseDBEntity> dbClass) throws SQLException {
        createOrAlterTable(ConfigUtil.properties2Object(GameContext.DEFAULT_CONFIG_FILE_NAME, DBConfig.class) , dbClass);
    }


    public static void createOrAlterTable(DBConfig dbConfig, Class<? extends BaseDBEntity> dbClass) throws SQLException {

        DBTableManager dbTableManager = new DBTableManager(new TypeHandlerRegistry());


        TableInfo tableInfo = dbTableManager.tryAddTableInfo(dbClass);
        

        if(!isTableExist(dbConfig , dbClass)){
            createTable(dbConfig , dbClass);
            return;
        }
        // alter table


        throw new SQLException(" wait implements ,  alter table not support now ");

    }








}
