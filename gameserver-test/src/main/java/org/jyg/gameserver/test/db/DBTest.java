package org.jyg.gameserver.test.db;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.event.listener.GameEventListener;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.ConfigUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;
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

        DBConfig dbConfig = ConfigUtil.properties2Object(GameContext.DEFAULT_CONFIG_FILE_NAME, DBConfig.class);
        if(dbConfig == null){
            throw new IllegalArgumentException();
        }

//        CreateTableUtil.createTable(Maik.class);

        DBGameConsumer consumer = new DBGameConsumer(dbConfig);
        consumer.setId(100);
        consumer.tryAddTableInfo(MaikDB.class);

        gameServerBootstrap.getGameContext().getConsumerManager().addConsumer(consumer);
        gameServerBootstrap.getGameContext().getMainGameConsumer().getInstanceManager()
                .putInstance(new ConsumerDBManager(gameServerBootstrap.getGameContext().getMainGameConsumer() , 100));






        gameServerBootstrap.getGameContext().getMainGameConsumer().getEventManager().addEventListener(new DBTestConsumerThreadStartEventListener() );



        gameServerBootstrap.start();

    }



    public static class DBTestConsumerThreadStartEventListener implements GameEventListener<ConsumerThreadStartEvent> {
        @Override
        public void onEvent(ConsumerThreadStartEvent event) {


            GameConsumer con = event.getGameConsumer();

            int id = 48;

            MaikDB maik = new MaikDB();
            maik.setId(id);
            maik.setContent("hello_world_31");
            maik.setAge(1000);
            maik.setPay(false);
            con.getInstanceManager().getInstance(ConsumerDBManager.class).insert(maik);




            con.getInstanceManager().getInstance(ConsumerDBManager.class).select(maik, (eventId, data) -> {
                MaikDB maik1 = (MaikDB)data;
                Logs.DEFAULT_LOGGER.info("select result : id {} content {} , pay {}" ,maik1.getId(), maik1.getContent() , maik1.isPay());
            });


            maik.setPay(true);

            con.getInstanceManager().getInstance(ConsumerDBManager.class).updateField(maik , "pay");

            con.getInstanceManager().getInstance(ConsumerDBManager.class).select(maik, (eventId, data) -> {
                MaikDB maik1 = (MaikDB)data;
                Logs.DEFAULT_LOGGER.info("select result : id {} content {} , pay {}" ,maik1.getId(), maik1.getContent() , maik1.isPay());
            });



        }
    }



}
