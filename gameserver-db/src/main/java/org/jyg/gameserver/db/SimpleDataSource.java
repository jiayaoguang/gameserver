package org.jyg.gameserver.db;

import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class SimpleDataSource implements DataSource , Closeable {


    private final DBConfig dbConfig;

    public SimpleDataSource(DBConfig dbConfig) {
        this.dbConfig = dbConfig;

        if(StringUtils.isNotEmpty(dbConfig.getDriverClassName())){
            try {
                Class.forName(dbConfig.getDriverClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public Connection getConnection(boolean autoCommit) throws SQLException {
        Connection connection = getConnection();
        connection.setAutoCommit(autoCommit);
        return connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbConfig.getJdbcUrl(), dbConfig.getUsername(), dbConfig.getPassword());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(dbConfig.getJdbcUrl(), username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("Can't support unwrap method!");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLException("Can't support unwrap method!");
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLException("Can't support getLogWriter method!");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
