package org.jyg.gameserver.test.db;

import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.consumer.ConsumerGroup;
import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.db.ConsumerDBManager;
import org.jyg.gameserver.db.DBConsumer;
import org.jyg.gameserver.db.DBConsumerGroup;

import java.util.List;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class DBConsumerGroupTest {


    public static void main(String[] args) throws Exception {

        GameServerBootstrap gameServerBootstrap = new GameServerBootstrap();
        gameServerBootstrap.addHttpConnector(8088);

        DBConsumerGroup dbConsumerGroup = new DBConsumerGroup();
//        consumerGroup.setId(100);
        dbConsumerGroup.addTableInfo(Maik.class);

        gameServerBootstrap.getContext().getConsumerManager().addConsumer(dbConsumerGroup);

        gameServerBootstrap.getContext().getDefaultConsumer().getInstanceManager()
                .putInstance(new ConsumerDBManager(gameServerBootstrap.getContext().getDefaultConsumer() , dbConsumerGroup.getDbConfig().getDbConsumerGroupId()));

        gameServerBootstrap.getContext().getDefaultConsumer().setConsumerStartHandler((consumer)->{


            consumer.getTimerManager().addUnlimitedTimer(1000L,1000L,()->{
               AllUtil.println(" hello ----------  ");
            });


            Maik maik = new Maik();
            maik.setId(23);
            maik.setContent("jjjjj");

            consumer.getInstanceManager().getInstance(ConsumerDBManager.class).delete(maik);

            maik.setContent("hello");

            consumer.getInstanceManager().getInstance(ConsumerDBManager.class).insert(maik);

            consumer.getInstanceManager().getInstance(ConsumerDBManager.class).select(maik, new ResultHandler() {
                @Override
                public void call(int eventId, Object data) {
                    AllUtil.println(Thread.currentThread().getName() + " : "+ ((Maik)data).getContent());
                }

//                @Override
//                public void onTimeout() {
//                    AllUtil.println("timeout");
//                }
            });

            Maik selectByMaikDb = new Maik();
            selectByMaikDb.setContent("asd");
            consumer.getInstanceManager().getInstance(ConsumerDBManager.class).selectBy(selectByMaikDb,"content", new ResultHandler<List<Maik>>() {
                @Override
                public void call(int eventId, List<Maik> data) {

                    for(Maik maik1 : data){
                        AllUtil.println(Thread.currentThread().getName() + " : "+ (maik1).getContent() + " _ " + maik1.getId());
                    }

                }

//                @Override
//                public void onTimeout() {
//                    AllUtil.println("timeout");
//                }
            } );

        });


        gameServerBootstrap.start();

    }


}
