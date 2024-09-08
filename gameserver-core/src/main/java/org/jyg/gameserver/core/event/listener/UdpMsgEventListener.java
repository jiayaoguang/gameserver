package org.jyg.gameserver.core.event.listener;

import io.netty.channel.Channel;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.UdpMsgEvent;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.session.UdpChannelSession;

/**
 * create by jiayaoguang on 2024/9/8
 */
public class UdpMsgEventListener implements GameEventListener<UdpMsgEvent> {
    private final GameConsumer gameConsumer;

    public UdpMsgEventListener(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }


    Channel channel;

    @Override
    public void onEvent(UdpMsgEvent event) {


//        if(event.getChannel() instanceof NioDatagramChannel){
//            gameConsumer.processEventMsg(null, event);
//            return;
//        }


        //TDO 考虑是否要复用session
        Session session = new UdpChannelSession(0 , event.getChannel(),event.getSenderHost(),event.getSenderPort());
        gameConsumer.processEventMsg(session, event);

    }
}