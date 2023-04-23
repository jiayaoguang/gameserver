package org.jyg.gameserver.test.db.proxy;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.MpscQueueGameConsumer;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.db.ConsumerDBManager;
import org.jyg.gameserver.db.DBConfig;
import org.jyg.gameserver.db.DBGameConsumer;
import org.jyg.gameserver.test.db.MaikDB;

/**
 * create by jiayaoguang on 2022/11/13
 */
public class DBProxy {


    public static final int DB_CONSUMER_ID = 100;

    public static void main(String[] args) {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC");
        dbConfig.setUsername("root");
        dbConfig.setPassword("123456");


        GameConsumer gameConsumer = new MpscQueueGameConsumer();
        gameConsumer.setId(101);


        GameServerBootstrap gameServerBootstrap = new GameServerBootstrap(gameConsumer);
//        gameServerBootstrap.getDefaultConsumer().setId(2);
        gameServerBootstrap.addTcpConnector(8888);

        DBGameConsumer consumer = new DBGameConsumer(dbConfig);
        consumer.setId(DB_CONSUMER_ID);
        consumer.tryAddTableInfo(MaikDB.class);

        gameServerBootstrap.getGameContext().getConsumerManager().addConsumer(consumer);
        gameServerBootstrap.getGameContext().getMainGameConsumer().getInstanceManager()
                .putInstance(new ConsumerDBManager(gameServerBootstrap.getGameContext().getMainGameConsumer() , 100));


        gameServerBootstrap.start();

    }
}
