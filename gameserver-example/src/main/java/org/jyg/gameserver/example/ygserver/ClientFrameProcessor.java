package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.event.MsgEvent;
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
    public void process(Session session, MsgEvent<ClientFrameMsg> event) {

        PlayerManager playerManager = getGameConsumer().getInstance(PlayerManager.class);
        Player player = playerManager.getLoginPlayerMap().get(session.getSessionId());
        if(player == null){
            return;
        }

//        FrameManager frameManager = getGameConsumer().getInstance(FrameManager.class);

//         session = getConsumer().getChannelManager().getSession(event.getChannel());

        PlayerFrameMsg playerFrameMsg = new PlayerFrameMsg();
        playerFrameMsg.setPlayerId(player.getPlayerDB().getId());
        playerFrameMsg.setPosi(event.getMsgData().getPosi());
        playerFrameMsg.setDir(event.getMsgData().getDir());
        playerFrameMsg.setBulletActive(event.getMsgData().bulletActive);
        playerFrameMsg.setBulletPosi(event.getMsgData().getBulletPosi());
        playerFrameMsg.setName(player.getPlayerDB().getName());

//        frameManager.getPlayerFrameMsgMap().put( session.getSessionId() , playerFrameMsg);

        Room room = getGameConsumer().getInstance(RoomManager.class).getRoom(player.getPlayerDB().getId());
        if(room == null){
            return;
        }
        RoomPlayer roomPlayer = room.getRoomPlayerMap().get(player.getPlayerDB().getId());
        if(roomPlayer != null && roomPlayer.getState() == 0){
            roomPlayer.setPosi(event.getMsgData().getPosi());
            roomPlayer.setDir(playerFrameMsg.getDir());
            if(roomPlayer.getRoom() != null){
                roomPlayer.getRoom().getPlayerFrameMsgMap().put( player.getPlayerDB().getId() , playerFrameMsg);
            }

        }


    }
}
