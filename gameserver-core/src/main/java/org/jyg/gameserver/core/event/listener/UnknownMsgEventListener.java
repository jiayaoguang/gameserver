package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.GameEventListener;
import org.jyg.gameserver.core.event.UnknownMsgEvent;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.core.util.UnknownMsgHandler;

public class UnknownMsgEventListener implements GameEventListener<UnknownMsgEvent> {
    private final GameConsumer gameConsumer;

    public UnknownMsgEventListener(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }

    @Override
    public void onEvent(UnknownMsgEvent event) {

        Session session = gameConsumer.getChannelManager().getSession(event.getChannel());
        if(session == null){
            Logs.DEFAULT_LOGGER.info("channel {} session not found , ignore msg {}" , AllUtil.getChannelRemoteAddr(event.getChannel()) , event.getMsgId());
            return;
        }

        UnknownMsgHandler unknownMsgHandler = gameConsumer.getUnknownMsgHandler();
        if(unknownMsgHandler == null){
            Logs.DEFAULT_LOGGER.error(" {} send unknown msg {}",AllUtil.getChannelRemoteAddr(event.getChannel()) , event.getMsgId());
            return;
        }

        unknownMsgHandler.process(session, event.getMsgId(), event.getMsgData());

    }
}
