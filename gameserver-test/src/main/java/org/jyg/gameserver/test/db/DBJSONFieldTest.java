package org.jyg.gameserver.test.db;

import com.alibaba.fastjson.JSONObject;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.db.DBConsumer;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class DBJSONFieldTest {


    public static void main(String[] args) {

        GameServerBootstrap gameServerBootstrap = new GameServerBootstrap();
        gameServerBootstrap.addHttpConnector(8080);

        DBConsumer consumer = new DBConsumer();
        consumer.setId(100);
        consumer.addTableInfo(Maik2.class);

        gameServerBootstrap.getContext().getConsumerManager().addConsumer(consumer);

        gameServerBootstrap.start();

        Maik2 maik2 = new Maik2();
        maik2.setId(24);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("123","qwe");
        maik2.setContent(jsonObject);

        gameServerBootstrap.getContext().getConsumerManager().getConsumer(100).publicEvent(EventType.DEFAULT_EVENT , maik2 , null , 1 );


        Maik2 maik21 = new Maik2();
        maik21.setId(25);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("123www","qwewwww");
        maik21.setContent(jsonObject2);
        gameServerBootstrap.getContext().getConsumerManager().getConsumer(100).publicEvent(EventType.DEFAULT_EVENT , maik21 , null , 1 );

    }



}
