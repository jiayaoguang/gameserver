package org.jyg.gameserver.test.db;

import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.ConfigUtil;
import org.jyg.gameserver.db.ConsumerDBManager;
import org.jyg.gameserver.db.DBConfig;
import org.jyg.gameserver.db.DBGameConsumer;

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
        DBGameConsumer consumer = new DBGameConsumer(dbConfig);
        consumer.setId(100);
        consumer.addTableInfo(Maik.class);

        gameServerBootstrap.getGameContext().getConsumerManager().addConsumer(consumer);
        gameServerBootstrap.getGameContext().getDefaultGameConsumer().getInstanceManager()
                .putInstance(new ConsumerDBManager(gameServerBootstrap.getGameContext().getDefaultGameConsumer() , 100));

        gameServerBootstrap.start();

        Maik maik = new Maik();
        maik.setId(25);
        maik.setContent("hello");

        gameServerBootstrap.getGameContext().getDefaultGameConsumer().getInstanceManager().getInstance(ConsumerDBManager.class).insert(maik);
    }



}
