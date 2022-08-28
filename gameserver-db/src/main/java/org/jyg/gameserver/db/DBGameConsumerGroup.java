package org.jyg.gameserver.db;

import io.netty.channel.Channel;
import org.jyg.gameserver.core.consumer.GameConsumerGroup;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.util.ConfigUtil;
import org.jyg.gameserver.db.type.TypeHandler;

/**
 * create by jiayaoguang on 2021/5/16
 */
public class DBGameConsumerGroup extends GameConsumerGroup<DBGameConsumer> {

    private final DBConfig dbConfig;

    public DBGameConsumerGroup() {
        this(ConfigUtil.properties2Object("jyg.properties", DBConfig.class));
    }


    public DBGameConsumerGroup(DBConfig dbConfig) {

        this.dbConfig = dbConfig;

        if(this.dbConfig == null){
            throw new IllegalArgumentException("dbconfig error");
        }

//        Configuration configuration = new Configuration();
//
//        SimpleDataSource simpleDataSource = new SimpleDataSource();
//        simpleDataSource.setUrl(this.dbConfig.getJdbcUrl());
//        simpleDataSource.setUser(this.dbConfig.getUsername());
//        simpleDataSource.setPass(this.dbConfig.getPassword());
//
//        Environment environment = new Environment("1", new JdbcTransactionFactory(), simpleDataSource);
//        configuration.setEnvironment(environment);
//        configuration.addMapper(MybatisDao.class);


        for(int i=0;i<dbConfig.getDbConsumerNum();i++){
            DBGameConsumer dbConsumer = new DBGameConsumer(dbConfig);
//            dbConsumer.setDbConfig(dbConfig);
            addChildConsumer(dbConsumer);
        }

        setId(dbConfig.getDbConsumerGroupId());

    }




    public void addTableInfo(Class<?> dbEntity) {
        for (DBGameConsumer childDBConsumer : getChildConsumerList()) {
            childDBConsumer.addTableInfo(dbEntity);
        }
    }

    @Override
    protected void processDefaultEvent(int eventId , EventData eventData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void publicEvent(EventType evenType, Object data, Channel channel, int eventId, EventExtData eventExtData) {
        if(!(data instanceof BaseDBEntity)){
            throw new IllegalArgumentException("db entity must extend BaseDBEntity");
        }
        Object cloneData = ((BaseDBEntity) data).clone();

        super.publicEvent(evenType , cloneData , channel , eventId , eventExtData);
    }

    public void registerTypeHandler(Class<?> clazz , TypeHandler<?> typeHandler){

        if(isStart()){
            throw new IllegalStateException("registerTypeHandler fail isStart");
        }

        for (DBGameConsumer childDBConsumer : getChildConsumerList()) {
            childDBConsumer.registerTypeHandler(clazz, typeHandler);
        }
    }


    public DBConfig getDbConfig() {
        return dbConfig;
    }

}