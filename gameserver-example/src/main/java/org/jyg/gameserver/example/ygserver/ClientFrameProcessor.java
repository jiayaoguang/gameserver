package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.example.ygserver.msg.ClientFrameMsg;
import org.jyg.gameserver.example.ygserver.msg.PlayerFrameMsg;

/**
 * create by jiayaoguang on 2021/9/11
 */
public class ClientFrameProcessor extends ByteMsgObjProcessor<ClientFrameMsg> {


    public ClientFrameProcessor() {
        super(ClientFrameMsg.class);
    }

    @Override
    public void process(Session session, EventData<ClientFrameMsg> event) {

        PlayerManager playerManager = getGameConsumer().getInstance(PlayerManager.class);
        Player player = playerManager.getLoginPlayerMap().get(session.getSessionId());
        if(player == null){
            return;
        }

        FrameManager frameManager = getGameConsumer().getInstance(FrameManager.class);

//         session = getConsumer().getChannelManager().getSession(event.getChannel());

        PlayerFrameMsg playerFrameMsg = new PlayerFrameMsg();
        playerFrameMsg.setPlayerId(player.getPlayerDB().getId());
        playerFrameMsg.setPosi(event.getData().getPosi());
        playerFrameMsg.setBulletActive(event.getData().bulletActive);
        playerFrameMsg.setBulletPosi(event.getData().getBulletPosi());
        playerFrameMsg.setName(player.getPlayerDB().getName());

        frameManager.getPlayerFrameMsgMap().put( session.getSessionId() , playerFrameMsg);

        RoomPlayer roomPlayer = getGameConsumer().getInstance(RoomManager.class).getRoom().getRoomPlayerMap().get(player.getPlayerDB().getId());
        if(roomPlayer != null){
            roomPlayer.setPosi(event.getData().getPosi());
        }


    }
}
