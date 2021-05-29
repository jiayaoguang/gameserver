package org.jyg.gameserver.db;

/**
 * create by jiayaoguang at 2021/5/22
 */
public class DBConfig {

    private String jdbcUrl;
    private String username;
    private String password;

    private String driverClassName;

    private int dbConsumerNum = 3;
    private int dbConsumerGroupId = 100;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public int getDbConsumerNum() {
        return dbConsumerNum;
    }

    public void setDbConsumerNum(int dbConsumerNum) {
        this.dbConsumerNum = dbConsumerNum;
    }

    public int getDbConsumerGroupId() {
        return dbConsumerGroupId;
    }

    public void setDbConsumerGroupId(int dbConsumerGroupId) {
        this.dbConsumerGroupId = dbConsumerGroupId;
    }


    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
}
