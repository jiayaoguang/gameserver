package org.jyg.gameserver.db;

import org.jyg.gameserver.core.consumer.GameConsumerGroup;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.ConsumerDefaultEvent;
import org.jyg.gameserver.core.util.ConfigUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.db.type.TypeHandler;

/**
 * create by jiayaoguang on 2021/5/16
 */
public class DBGameConsumerGroup extends GameConsumerGroup<DBGameConsumer> {

    private final DBConfig dbConfig;

    public DBGameConsumerGroup() {
        this(ConfigUtil.properties2Object(GameContext.DEFAULT_CONFIG_FILE_NAME, DBConfig.class));
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

        setId(dbConfig.getDbConsumerGroupId());

        for(int i=0;i<dbConfig.getDbConsumerNum();i++){
            DBGameConsumer dbConsumer = new DBGameConsumer(dbConfig , false);
//            dbConsumer.setDbConfig(dbConfig);
            addChildConsumer(dbConsumer);
        }



    }




    public void tryAddTableInfo(Class<?> dbEntity) {
        for (DBGameConsumer childDBConsumer : getChildConsumerList()) {
            childDBConsumer.tryAddTableInfo(dbEntity);
        }
    }

    @Override
    public void processDefaultEvent(ConsumerDefaultEvent eventData) {

        throw new UnsupportedOperationException();
    }

    @Override
    public void publicEvent(EventData eventData) {
//        if(!(data instanceof BaseDBEntity)){
//            throw new IllegalArgumentException("db entity must extend BaseDBEntity");
//        }

        if(eventData.getEvent() instanceof ConsumerDefaultEvent){
            ConsumerDefaultEvent consumerDefaultEvent = (ConsumerDefaultEvent) eventData.getEvent();
            if(consumerDefaultEvent.getData() instanceof BaseDBEntity){
                consumerDefaultEvent.setData(((BaseDBEntity) consumerDefaultEvent.getData()).clone());
            }

        }

        super.publicEvent(eventData);
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
