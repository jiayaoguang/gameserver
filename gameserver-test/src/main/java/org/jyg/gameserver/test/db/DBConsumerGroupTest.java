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

/**
 * create by jiayaoguang on 2021/5/15
 */
public class DBConsumerGroupTest {


    public static void main(String[] args) throws Exception {

        GameServerBootstrap gameServerBootstrap = new GameServerBootstrap();
        gameServerBootstrap.addHttpConnector(8088);

        DBConsumerGroup consumerGroup = new DBConsumerGroup();
        consumerGroup.setId(100);
        consumerGroup.addTableInfo(Maik.class);

        gameServerBootstrap.getContext().getConsumerManager().addConsumer(consumerGroup);

        gameServerBootstrap.getContext().getDefaultConsumer().getInstanceManager()
                .putInstance(new ConsumerDBManager(gameServerBootstrap.getContext().getDefaultConsumer() , 100));

        gameServerBootstrap.start();

        Maik maik = new Maik();
        maik.setId(23);
        maik.setContent("jjjjj");


        gameServerBootstrap.getContext().getDefaultConsumer().getInstanceManager().getInstance(ConsumerDBManager.class).delete(maik);

        gameServerBootstrap.getContext().getDefaultConsumer().getInstanceManager().getInstance(ConsumerDBManager.class).insert(maik);

        gameServerBootstrap.getContext().getDefaultConsumer().getInstanceManager().getInstance(ConsumerDBManager.class).select(maik, new ResultHandler() {
            @Override
            public void call(int eventId, Object data) {
                AllUtil.println(((Maik)data).getContent());
            }

            @Override
            public void onTimeout() {
                AllUtil.println("timeout");
            }
        });
    }



}
