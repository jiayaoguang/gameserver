package org.jyg.gameserver.db;

import cn.hutool.core.collection.CollectionUtil;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.field.IFieldOperator;
import org.jyg.gameserver.core.field.UnsafeFieldOperator;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.db.type.TypeHandler;
import org.jyg.gameserver.db.type.TypeHandlerRegistry;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlExecutor {

    private final SimpleDataSource simpleDataSource;
    private final TypeHandlerRegistry typeHandlerRegistry;

    private Connection connection;


    public SqlExecutor(SimpleDataSource simpleDataSource, TypeHandlerRegistry typeHandlerRegistry) {
        this.typeHandlerRegistry = typeHandlerRegistry;
        this.simpleDataSource = simpleDataSource;

    }

    public void tryConnectIfClose() {
        if (connection == null) {
            try {
                connection = simpleDataSource.getConnection();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new IllegalArgumentException(e);
            }
            return;
        }


        boolean isClose = false;

        try {
            isClose = connection.isClosed();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            isClose = true;
        }


        if (isClose) {
            closeQuiet();
            try {
                connection = simpleDataSource.getConnection();
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }


    }

    public void closeQuiet() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public Object executeSql(PrepareSQLAndParams prepareSQLAndParams, Class<?> dbEntityClass, TableInfo tableInfo) throws SQLException, IllegalAccessException, InstantiationException {
        tryConnectIfClose();


        try (PreparedStatement preparedStatement = connection.prepareStatement(prepareSQLAndParams.prepareSQL);) {

            fillPreparedStatement(preparedStatement , prepareSQLAndParams.paramValues);

            switch (prepareSQLAndParams.sqlExecuteType) {
                case QUERY_ONE: {
                    List<Object> returnList = executeQuery(dbEntityClass, tableInfo, preparedStatement);
                    if (returnList.isEmpty()) {
                        return null;
                    }
                    return returnList.get(0);
                }
                case QUERY_MANY: {
                    List<Object> returnList = executeQuery(dbEntityClass, tableInfo, preparedStatement);
                    return returnList;
                }
                case MODIFY:{
                    int modifyNum = preparedStatement.executeUpdate();

                    if (!connection.getAutoCommit()) {
                        connection.commit();
                    }

                    return modifyNum;
                }
                case EXECUTE:{
                    boolean result = preparedStatement.execute();

                    if (!connection.getAutoCommit()) {
                        connection.commit();
                    }

                    return result;
                }
                default:
                    throw new IllegalArgumentException("unknown sqlExecuteType : " + prepareSQLAndParams.sqlExecuteType);

            }

        }

    }


    private List<Object> executeQuery(Class<?> dbEntityClass, TableInfo tableInfo, PreparedStatement preparedStatement) throws SQLException, InstantiationException, IllegalAccessException {
        List<Object> returnList = new ArrayList<>();
        try (ResultSet resultSet = preparedStatement.executeQuery();) {

            while (!resultSet.isClosed() && resultSet.next()) {


                Object returnObj = dbEntityClass.newInstance();

                for (TableFieldInfo tableFieldInfo : tableInfo.getFieldInfoLinkedMap().values()) {

                    TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(tableFieldInfo.getClassField().getType());
                    if (typeHandler == null) {
                        Logs.DB.error("typeHandler == null class field {}", tableFieldInfo.getClassField().getType());
                        continue;
                    }

                    Object value = typeHandler.getNullableResult(resultSet, tableFieldInfo.getTableFieldName());

                    tableFieldInfo.getClassField().set(returnObj, value);
                }

                returnList.add(returnObj);
            }
        }
        return returnList;
    }
    public Object executeSql(PrepareSQLAndParams prepareSQLAndParams) throws SQLException {
        return executeSql( prepareSQLAndParams.prepareSQL,prepareSQLAndParams.paramValues,prepareSQLAndParams.sqlExecuteType );
    }


    public Object executeSql(String prepareSql,List<Object> params,SqlExecuteType executeType) throws SQLException {

        tryConnectIfClose();

        try (PreparedStatement preparedStatement = connection.prepareStatement(prepareSql);) {
            fillPreparedStatement(preparedStatement , params);

            switch (executeType) {

                case QUERY_ONE:
                case QUERY_MANY: {
                    List<Map<String, Object>> returnList = new ArrayList<>();
                    try (ResultSet resultSet = preparedStatement.executeQuery();) {

                        while (!resultSet.isClosed() && resultSet.next()) {

                            Map<String, Object> columnValueMap = new HashMap<>();

                            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {

                                int columnIndex = i + 1;

                                String columnName = resultSet.getMetaData().getColumnName(columnIndex);
                                Object value = resultSet.getObject(columnIndex);

                                columnValueMap.put(columnName, value);

                            }

                            returnList.add(columnValueMap);
                        }
                    }
                    if (executeType == SqlExecuteType.QUERY_ONE) {
                        if (returnList.isEmpty()) {
                            return null;
                        } else {
                            return returnList.get(0);
                        }
                    }

                    return returnList;
                }

                case MODIFY:{
                    int modifyNum = preparedStatement.executeUpdate();

                    if (!connection.getAutoCommit()) {
                        connection.commit();
                    }

                    return modifyNum;
                }
                case EXECUTE: {
                    boolean result = preparedStatement.execute();

                    if (!connection.getAutoCommit()) {
                        connection.commit();
                    }

                    return result;
                }
                default:
                    throw new IllegalArgumentException("unknown sqlExecuteType : " + executeType);
            }


        }

    }



    public void execQueryTest(){
        tryConnectIfClose();

        try (Statement statement  = connection.createStatement();){
            statement.execute("SELECT 1;");
        } catch (Exception e) {
            Logs.DEFAULT_LOGGER.error("execQueryTest exception ",e);
            tryConnectIfClose();
        }

    }


    @SuppressWarnings({"unchecked" , "rawtypes"})
    private void fillPreparedStatement(PreparedStatement preparedStatement,List<Object> params) throws SQLException {
        if (CollectionUtil.isEmpty(params)) {
            return;
        }
        for (int i = 0; i < params.size(); i++) {
            int parameterIndex = i + 1;
            Object value = params.get(i);


            TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(value.getClass());
            if (typeHandler != null) {
                typeHandler.setParameter(preparedStatement, parameterIndex, value);
            } else {
                preparedStatement.setObject(parameterIndex, value);
            }
        }

    }


}
