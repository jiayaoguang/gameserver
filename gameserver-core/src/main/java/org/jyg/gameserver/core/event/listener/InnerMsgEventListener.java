package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.InnerMsgEvent;

public class InnerMsgEventListener implements GameEventListener<InnerMsgEvent> {
    private final GameConsumer gameConsumer;

    public InnerMsgEventListener(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }

    @Override
    public void onEvent(InnerMsgEvent event) {

//        Session session = gameConsumer.getChannelManager().getSession(event.getChannel());
//        if(session == null){
//            Logs.DEFAULT_LOGGER.info("channel {} session not found , ignore msg {}" , AllUtil.getChannelRemoteAddr(event.getChannel()) , event.getMsgId());
//            return;
//        }
        gameConsumer.processEventMsg(null, event);

    }
}
