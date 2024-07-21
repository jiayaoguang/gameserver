package org.jyg.gameserver.db.util;

import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.ConfigUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.db.*;
import org.jyg.gameserver.db.type.TypeHandlerRegistry;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        String createSql = getCreateTableSql(dbClass);

        try(Connection connection = getConn(dbConfig);
            Statement statement = connection.createStatement();
        ){



            statement.execute(createSql);

        }catch (Exception e){
            Logs.DEFAULT_LOGGER.error("execute create table fail , sql {}",createSql);
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
                    .append("(").append(tableFieldInfo.getLength()).append(")").append(" NOT NULL ");

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
            String uniqueSql = "ALTER TABLE `" + tableName + "` ADD UNIQUE INDEX `unique_"+ tableName +"_"+ fieldName +" `( \n" +
                    "`" + fieldName + "` \n" +
                    ") ";
            return uniqueSql;
        }

        if (fieldIndexType == FieldIndexType.INDEX) {
            String indexSql = "ALTER TABLE `" + tableName + "` ADD INDEX `index_"+ tableName +"_"+ fieldName +" `( \n" +
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

        alterTable(dbConfig , dbClass);


    }

    public static void alterTable(DBConfig dbConfig, Class<? extends BaseDBEntity> dbClass) throws SQLException {
//        throw new SQLException(" wait implements ,  alter table not support now ");
        DBTableManager dbTableManager = new DBTableManager(new TypeHandlerRegistry());

        TableInfo tableInfo = dbTableManager.tryAddTableInfo(dbClass);

        String descSql = "DESC " + tableInfo.getTableName() + ";";
        String queryIndexSql = "SHOW INDEX FROM  " + tableInfo.getTableName() + ";";

        Map<String,String> field2typeMap = new HashMap<>();

        Map<String,String> fieldIndexMap = new HashMap<>();

        try(Connection connection = getConn(dbConfig);
            Statement statement = connection.createStatement();
        ){

            ResultSet descResultSet = statement.executeQuery(descSql);
            while (descResultSet != null && descResultSet.next()){
                String field = descResultSet.getString(1);
                String type = descResultSet.getString(2);

                field2typeMap.put(field , type);

            }

            ResultSet queryIndexResultSet = statement.executeQuery(queryIndexSql);
            while (queryIndexResultSet != null && queryIndexResultSet.next()){
                String field = queryIndexResultSet.getString("Column_name");
                fieldIndexMap.put(field , "");
            }


        }

        List<TableFieldInfo> newFiledInfos = new ArrayList<>();

        List<TableFieldInfo> changeTypes = new ArrayList<>();

//        List<TableFieldInfo> changeLens = new ArrayList<>();

        List<TableFieldInfo> needAddIndexFields = new ArrayList<>();

        for(TableFieldInfo tableFieldInfo : tableInfo.getFieldInfoLinkedMap().values()){
            if(!field2typeMap.containsKey(tableFieldInfo.getTableFieldName())){
                //新字段
                newFiledInfos.add(tableFieldInfo);
            }else {
                String typeAndLen = field2typeMap.get(tableFieldInfo.getTableFieldName());
                String type = typeAndLen.substring(0, typeAndLen.indexOf('('));
                int len = Integer.parseInt(typeAndLen.substring(type.length()+1, typeAndLen.length()-1));
                if(!tableFieldInfo.getFieldType().getName().equalsIgnoreCase(type)){
                    changeTypes.add(tableFieldInfo);
                }else if(tableFieldInfo.getLength() != len) {
                    changeTypes.add(tableFieldInfo);
                }
            }


            if(tableFieldInfo.getDbTableFieldAnno()!= null && tableFieldInfo.getDbTableFieldAnno().indexType() != FieldIndexType.NONE){

                if(!fieldIndexMap.containsKey(tableFieldInfo.getTableFieldName())){
                    needAddIndexFields.add(tableFieldInfo);
                }

            }


        }

        try(Connection connection = getConn(dbConfig);
            Statement statement = connection.createStatement();
        ){

            for(TableFieldInfo tableFieldInfo : newFiledInfos ){
                String addFieldSql = "ALTER TABLE "+ tableInfo.getTableName() +" ADD "+ tableFieldInfo.getTableFieldName() + " "
                        + tableFieldInfo.getFieldType().getName() +"("+ tableFieldInfo.getLength() +") ";
                Logs.DB.info("exec add field sql : {} " , addFieldSql );
                statement.execute(addFieldSql);

            }

            for(TableFieldInfo tableFieldInfo : changeTypes ){
                String changeSql = "ALTER TABLE `"+ tableInfo.getTableName() +"` CHANGE `"+ tableFieldInfo.getTableFieldName() +"` `"
                        + tableFieldInfo.getTableFieldName() +"` "+ tableFieldInfo.getFieldType().getName() +"("+ tableFieldInfo.getLength() +") ";
                Logs.DB.info("exec change field type sql : {} " , changeSql );
                statement.execute(changeSql);

            }


            for(TableFieldInfo tableFieldInfo : needAddIndexFields){
                if(tableFieldInfo.getDbTableFieldAnno() != null && tableFieldInfo.getDbTableFieldAnno().indexType() != FieldIndexType.NONE){
                    String indexSql = getIndexSql(tableInfo.getTableName(), tableFieldInfo.getTableFieldName(), tableFieldInfo.getFieldIndexType());
                    Logs.DB.info("exec indexSql : {} " , indexSql );
                    statement.execute(indexSql);
                }
            }

        }




    }


}

