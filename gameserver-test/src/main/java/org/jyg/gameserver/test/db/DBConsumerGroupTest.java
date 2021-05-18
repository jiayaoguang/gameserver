package org.jyg.gameserver.test.db;

import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.consumer.ConsumerGroup;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.db.DBConsumer;
import org.jyg.gameserver.db.DBConsumerGroup;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class DBConsumerGroupTest {


    public static void main(String[] args) {

        GameServerBootstrap gameServerBootstrap = new GameServerBootstrap();
        gameServerBootstrap.addHttpConnector(8080);

        DBConsumerGroup consumerGroup = new DBConsumerGroup(2);
        consumerGroup.setId(100);
        consumerGroup.addTableInfo(Maik.class);

        gameServerBootstrap.getContext().getConsumerManager().addConsumer(consumerGroup);

        gameServerBootstrap.start();

        Maik maik = new Maik();
        maik.setId(23);
        maik.setContent("world");

        gameServerBootstrap.getContext().getConsumerManager().getConsumer(100).publicEvent(EventType.DEFAULT_EVENT , maik , null , 1 ,new EventExtData(0,0,1));

    }



}
