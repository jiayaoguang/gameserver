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


        Room room = roomManager.getRoom(player.getPlayerDB().getId());


        if(room != null){
            RoomPlayer roomPlayer = room.getRoomPlayerMap().get(player.getPlayerDB().getId());
            roomPlayer.setPlayer(player);
            sendRoomInfoMsg(roomManager, roomPlayer);
            return;
        }





        if(roomManager.getWaitBattlePlayer() == null){
            roomManager.setWaitBattlePlayer(player);
            player.sendTip("正在匹配");
            return;
        }


        if(roomManager.getWaitBattlePlayer().getSession().getSessionId() == player.getSession().getSessionId()){
            player.sendTip("正在匹配 请勿重复请求");
            return;

        }

        Player player2 = roomManager.getWaitBattlePlayer();
        room = roomManager.enterRoom(  player , player2);



        RoomPlayer roomPlayer1 = room.getRoomPlayerMap().get(player.getPlayerDB().getId());
        sendRoomInfoMsg(roomManager, roomPlayer1);
        RoomPlayer roomPlayer2 = room.getRoomPlayerMap().get(player2.getPlayerDB().getId());

        sendRoomInfoMsg(roomManager, roomPlayer2);


    }

    private void sendRoomInfoMsg(RoomManager roomManager,  RoomPlayer roomPlayer) {
        Player player = roomPlayer.getPlayer();
        Session session = roomPlayer.getPlayer().getSession();

        SCEnterRoomMsg enterRoomMsg = new SCEnterRoomMsg();
        enterRoomMsg.setScore(roomPlayer.getScore());


        Room room = roomPlayer.getRoom();

        enterRoomMsg.wallMsgs.addAll(room.getWallMsgList());


        SCPlayerJoinMsg playerJoinMsg = new SCPlayerJoinMsg();

        PlayerInfoMsg myPlayerInfoMsg = new PlayerInfoMsg();
        myPlayerInfoMsg.setPlayerId(roomPlayer.getPlayer().getPlayerDB().getId());
        myPlayerInfoMsg.setPosi(roomPlayer.getPosi());
        myPlayerInfoMsg.setName(roomPlayer.getPlayer().getPlayerDB().getName());
        myPlayerInfoMsg.setHp(roomPlayer.getHp());
        playerJoinMsg.setPlayerInfoMsg(myPlayerInfoMsg);



        for (RoomPlayer other : room.getRoomPlayerMap().values()) {
            PlayerInfoMsg playerInfoMsg = new PlayerInfoMsg();
            playerInfoMsg.setPlayerId(other.getPlayer().getPlayerDB().getId());
            playerInfoMsg.setPosi(other.getPosi());
            playerInfoMsg.setName(other.getPlayer().getPlayerDB().getName());
            playerInfoMsg.setHp(other.getHp());

            enterRoomMsg.getPlayerInfoMsgs().add(playerInfoMsg);

            if (player.getPlayerDB().getId() != other.getPlayer().getPlayerDB().getId()) {
                other.getPlayer().getSession().writeMessage(playerJoinMsg);
            }

            for (Motion motion : other.getMotionMap().values()) {
                enterRoomMsg.getMotionMsgs().add(room.createMotionMsg(other, motion));
            }

        }

        for (Motion motion : room.getSysMotionMap().values()) {
            enterRoomMsg.getMotionMsgs().add(room.createMotionMsg(0, motion));
        }


        session.writeMessage(enterRoomMsg);
    }
}
