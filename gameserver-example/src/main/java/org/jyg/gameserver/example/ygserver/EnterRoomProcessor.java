package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.example.ygserver.msg.*;

public class EnterRoomProcessor extends ByteMsgObjProcessor<CSEnterRoomMsg> {

    public EnterRoomProcessor() {
        super(CSEnterRoomMsg.class);
    }

    @Override
    public void process(Session session, EventData<CSEnterRoomMsg> event) {


        RoomManager roomManager = this.getGameConsumer().getInstanceManager().getInstance(RoomManager.class);

        Player player = session.getSessionObject();

        RoomPlayer roomPlayer = roomManager.enterRoom(player);



        SCEnterRoomMsg enterRoomMsg = new SCEnterRoomMsg();
        enterRoomMsg.setScore(roomPlayer.getScore());

        enterRoomMsg.wallMsgs.addAll(roomManager.getRoom().getWallMsgList());


        SCPlayerJoinMsg playerJoinMsg = new SCPlayerJoinMsg();

        PlayerInfoMsg myPlayerInfoMsg = new PlayerInfoMsg();
        myPlayerInfoMsg.setPlayerId(roomPlayer.getPlayer().getPlayerDB().getId());
        myPlayerInfoMsg.setPosi(roomPlayer.getPosi());
        myPlayerInfoMsg.setName(roomPlayer.getPlayer().getPlayerDB().getName());
        myPlayerInfoMsg.setHp(roomPlayer.getHp());
        playerJoinMsg.setPlayerInfoMsg(myPlayerInfoMsg);

        for(RoomPlayer other : roomManager.getRoom().getRoomPlayerMap().values()){
            PlayerInfoMsg playerInfoMsg = new PlayerInfoMsg();
            playerInfoMsg.setPlayerId(other.getPlayer().getPlayerDB().getId());
            playerInfoMsg.setPosi(other.getPosi());
            playerInfoMsg.setName(other.getPlayer().getPlayerDB().getName());
            playerInfoMsg.setHp(other.getHp());

            enterRoomMsg.getPlayerInfoMsgs().add(playerInfoMsg);

            if(player.getPlayerDB().getId() != other.getPlayer().getPlayerDB().getId()){
                other.getPlayer().getSession().writeMessage(playerJoinMsg);
            }

            for(Motion motion : roomPlayer.getMotionMap().values()){
                enterRoomMsg.getMotionMsgs().add( roomManager.getRoom().createMotionMsg(roomPlayer , motion));
            }

        }


        session.writeMessage(enterRoomMsg);





    }
}
