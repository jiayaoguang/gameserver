package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class HttpRequestEventListener implements GameEventListener<HttpRequestEvent>{

    private final GameConsumer gameConsumer;


    public HttpRequestEventListener(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }

    @Override
    public void onEvent(HttpRequestEvent httpRequestEvent) {


        gameConsumer.processHttpEvent( httpRequestEvent.getEventData());

//        new ChannelMsgEventListener(null ,new EventData<>());


    }
}
