package org.jyg.gameserver.test.db.proxy;

import org.jyg.gameserver.core.consumer.*;
import org.jyg.gameserver.core.data.RemoteConsumerInfo;
import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.event.listener.GameEventListener;
import org.jyg.gameserver.core.exception.RequestTimeoutException;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.db.ConsumerDBManager;
import org.jyg.gameserver.test.db.MaikDB;

import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2022/11/13
 */
public class UseRemoteDBTest {


    public static void main(String[] args) {
        MpscQueueGameConsumer mainConsumer = new MpscQueueGameConsumer();
        mainConsumer.setId(1001);

        GameServerBootstrap gameServerBootstrap = new GameServerBootstrap(mainConsumer);
        gameServerBootstrap.addTcpConnector(8889);


        RemoteConsumerInfo remoteConsumerInfo = new RemoteConsumerInfo();
        remoteConsumerInfo.setIp("127.0.0.1" );
        remoteConsumerInfo.setPort(8888);
        remoteConsumerInfo.setConsumerId(DBProxy.DB_CONSUMER_ID);

        RemoteManagerGameConsumer consumer = new RemoteManagerGameConsumer(gameServerBootstrap.getGameContext() , remoteConsumerInfo);
//        consumer.setId(101);
//        consumer.addRemoteConsumerInfo(DBProxy.DB_CONSUMER_ID , "127.0.0.1" , 8888);

        gameServerBootstrap.getGameContext().getConsumerManager().addConsumer(consumer);
        gameServerBootstrap.getGameContext().getMainGameConsumer().getInstanceManager()
                .putInstance(new ConsumerDBManager(gameServerBootstrap.getGameContext().getMainGameConsumer() , DBProxy.DB_CONSUMER_ID));

        gameServerBootstrap.getGameContext().getMainGameConsumer().getEventManager().addEventListener(new DBProxyTestConsumerThreadStartEventListener() );

        gameServerBootstrap.start();
    }



    public static class DBProxyTestConsumerThreadStartEventListener implements GameEventListener<ConsumerThreadStartEvent> {
        @Override
        public void onEvent(ConsumerThreadStartEvent event) {


            GameConsumer con = event.getGameConsumer();

            int id = 40;

            MaikDB maik = new MaikDB();
            maik.setId(id);
            maik.setContent("hello_world_"+id);
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
            AllUtil.println("result _ waitForResult " + result);
            if(result instanceof List){
                List list = (List) result;
                if(list.size() >0 &&list.get(0) instanceof MaikDB){
                    MaikDB maik1 = (MaikDB)list.get(0);
                    AllUtil.println("waitForResult : " + maik1.getContent());
                }
            }


        }
    }

}
