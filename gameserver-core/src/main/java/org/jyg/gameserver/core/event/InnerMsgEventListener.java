package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.session.LocalSession;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class InnerMsgEventListener implements GameEventListener<InnerMsgEvent>{

    private final GameConsumer gameConsumer;


    public InnerMsgEventListener(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }

    @Override
    public void onEvent(InnerMsgEvent innerMsgEvent) {
        Session session = new LocalSession(innerMsgEvent.getEventData().getFromConsumerId(), gameConsumer.getGameContext());
        gameConsumer.processEventMsg(session , innerMsgEvent.getEventData());

//        new ChannelMsgEventListener(null ,new EventData<>());


    }
}
