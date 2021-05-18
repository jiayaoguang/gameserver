package org.jyg.gameserver.test.db;

import org.junit.Test;
import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.db.DBConsumer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class DBTest {


    public static void main(String[] args) {

        GameServerBootstrap gameServerBootstrap = new GameServerBootstrap();
        gameServerBootstrap.addHttpConnector(8080);

        DBConsumer consumer = new DBConsumer();
        consumer.setId(100);
        consumer.addTableInfo(Maik.class);

        gameServerBootstrap.getContext().getConsumerManager().addConsumer(consumer);

        gameServerBootstrap.start();

        Maik maik = new Maik();
        maik.setId(22);
        maik.setContent("hello");

        gameServerBootstrap.getContext().getConsumerManager().getConsumer(100).publicEvent(EventType.DEFAULT_EVENT , maik , null , 1 );

    }



}
