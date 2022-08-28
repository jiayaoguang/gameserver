package org.jyg.gameserver.db;

import cn.hutool.core.collection.CollectionUtil;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.field.IFieldOperator;
import org.jyg.gameserver.core.field.UnsafeFieldOperator;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.db.type.TypeHandler;
import org.jyg.gameserver.db.type.TypeHandlerRegistry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                connection = simpleDataSource.getConnection(false);
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
                connection = simpleDataSource.getConnection(false);
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

            if (!CollectionUtil.isEmpty(prepareSQLAndParams.paramValues)) {
                for (int i = 0; i < prepareSQLAndParams.paramValues.size(); i++) {
                    int parameterIndex = i + 1;
                    Object value = prepareSQLAndParams.paramValues.get(i);


                    TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(value.getClass());
                    if (typeHandler != null) {
                        typeHandler.setParameter(preparedStatement, parameterIndex, value);
                    } else {
                        preparedStatement.setObject(parameterIndex, value);
                    }
                }
            }

            switch (prepareSQLAndParams.sqlExecuteType) {
                case QUERY_ONE: {
                    List<Object> returnList = executeQuery(dbEntityClass, tableInfo, preparedStatement);
                    if (returnList.size() == 0) {
                        return null;
                    }
                    return returnList.get(0);
                }
                case QUERY_MANY: {
                    List<Object> returnList = executeQuery(dbEntityClass, tableInfo, preparedStatement);
                    return returnList;
                }
                case MODIFY:
                default:
                    int modifyNum = preparedStatement.executeUpdate();

                    if (!connection.getAutoCommit()) {
                        connection.commit();
                    }

                    return null;
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


}
