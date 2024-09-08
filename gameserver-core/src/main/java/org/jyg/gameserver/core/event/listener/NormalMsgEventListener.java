package org.jyg.gameserver.core.event.listener;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.NormalMsgEvent;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Logs;

public class NormalMsgEventListener implements GameEventListener<NormalMsgEvent> {
    private final GameConsumer gameConsumer;

    public NormalMsgEventListener(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }


    @Override
    public void onEvent(NormalMsgEvent event) {


        Session session = gameConsumer.getChannelManager().getSession(event.getChannel());
        if(session == null){
            Logs.DEFAULT_LOGGER.error("channel {} session not found , ignore msg {}" , AllUtil.getChannelRemoteAddr(event.getChannel()) , event.getMsgId());
            return;
        }
        gameConsumer.processEventMsg(session, event);

    }
}
