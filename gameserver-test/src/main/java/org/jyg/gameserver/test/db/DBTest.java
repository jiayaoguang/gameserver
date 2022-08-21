package org.jyg.gameserver.test.db;

import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.ConfigUtil;
import org.jyg.gameserver.db.ConsumerDBManager;
import org.jyg.gameserver.db.DBConfig;
import org.jyg.gameserver.db.DBConsumer;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class DBTest {


    public static void main(String[] args) throws Exception {

        GameServerBootstrap gameServerBootstrap = new GameServerBootstrap();
        gameServerBootstrap.addHttpConnector(8088);

        DBConfig dbConfig = ConfigUtil.properties2Object("jyg.properties", DBConfig.class);
        if(dbConfig == null){
            throw new IllegalArgumentException();
        }
        DBConsumer consumer = new DBConsumer(dbConfig);
        consumer.setId(100);
        consumer.addTableInfo(Maik.class);

        gameServerBootstrap.getGameContext().getConsumerManager().addConsumer(consumer);
        gameServerBootstrap.getGameContext().getDefaultConsumer().getInstanceManager()
                .putInstance(new ConsumerDBManager(gameServerBootstrap.getGameContext().getDefaultConsumer() , 100));

        gameServerBootstrap.start();

        Maik maik = new Maik();
        maik.setId(25);
        maik.setContent("hello");

        gameServerBootstrap.getGameContext().getDefaultConsumer().getInstanceManager().getInstance(ConsumerDBManager.class).insert(maik);
    }



}
