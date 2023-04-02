package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.HttpRequestEvent;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class HttpRequestEventListener implements GameEventListener<HttpRequestEvent> {

    private final GameConsumer gameConsumer;


    public HttpRequestEventListener(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }

    @Override
    public void onEvent(HttpRequestEvent httpRequestEvent) {


        gameConsumer.processHttpEvent( httpRequestEvent);

//        new ChannelMsgEventListener(null ,new EventData<>());


    }
}
