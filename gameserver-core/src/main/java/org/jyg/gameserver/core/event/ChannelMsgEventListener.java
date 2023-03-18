package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class ChannelMsgEventListener implements GameEventListener<ChannelMsgEvent>{

    private final GameConsumer gameConsumer;


    public ChannelMsgEventListener(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }

    @Override
    public void onEvent(ChannelMsgEvent channelMsgEvent) {
        Session session = gameConsumer.getChannelManager().getSession(channelMsgEvent.getChannel());
        if(session == null){
            Logs.DEFAULT_LOGGER.info("channel session is null");
            return;
        }

        gameConsumer.processEventMsg(session , channelMsgEvent.getEventData());

//        new ChannelMsgEventListener(null ,new EventData<>());


    }
}
