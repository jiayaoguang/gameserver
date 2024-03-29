package org.jyg.gameserver.test.db;

import org.jyg.gameserver.core.consumer.AbstractThreadQueueGameConsumer;
import org.jyg.gameserver.core.consumer.ConsumerFuture;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.event.listener.GameEventListener;
import org.jyg.gameserver.core.exception.RequestTimeoutException;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.ConfigUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.db.ConsumerDBManager;
import org.jyg.gameserver.db.DBConfig;
import org.jyg.gameserver.db.DBGameConsumer;

import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class DBWaitResultTest {


    public static void main(String[] args) throws Exception {

        GameServerBootstrap gameServerBootstrap = new GameServerBootstrap();
        gameServerBootstrap.addHttpConnector(8088);

        DBConfig dbConfig = ConfigUtil.properties2Object(GameContext.DEFAULT_CONFIG_FILE_NAME, DBConfig.class);
        if(dbConfig == null){
            throw new IllegalArgumentException();
        }
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

            int id = 33;

            MaikDB maik = new MaikDB();
            maik.setId(id);
            maik.setContent("hello_world_31");
            con.getInstanceManager().getInstance(ConsumerDBManager.class).insert(maik);

            List<Object> params = new ArrayList<>();
            params.add(id);


           long requestId =  con.getInstanceManager().getInstance(ConsumerDBManager.class)
                    .execQuerySql(MaikDB.class ,"select * from maik where id = ?;", params , (eventId , data)->{

                        List<MaikDB> maiks = (List<MaikDB>)data;
                        for( MaikDB maik1 : maiks ){
                            AllUtil.println("query result : " + maik1.getContent());
                        }

                    });

            ConsumerFuture consumerFuture = new ConsumerFuture(requestId ,(AbstractThreadQueueGameConsumer)con  );

            Object result = null;
            try {
                result = consumerFuture.waitForResult(10 * 1000L);
            } catch (RequestTimeoutException e) {
                throw new RuntimeException(e);
            }
            AllUtil.println("result _ waitForResult");
            if(result instanceof  List){
                List list = (List) result;
                if(list.size() >0 &&list.get(0) instanceof MaikDB){
                    MaikDB maik1 = (MaikDB)list.get(0);
                    AllUtil.println("waitForResult : " + maik1.getContent());
                }
            }


        }
    }



}
