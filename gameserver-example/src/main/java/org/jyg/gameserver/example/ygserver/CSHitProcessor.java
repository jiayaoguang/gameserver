package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.timer.ITimerHandler;
import org.jyg.gameserver.example.ygserver.msg.CSHitMsg;
import org.jyg.gameserver.example.ygserver.msg.PlayerFrameMsg;
import org.jyg.gameserver.example.ygserver.msg.SCHitMsg;
import org.jyg.gameserver.example.ygserver.msg.SCUpdatePlayerScoreMsg;

public class CSHitProcessor extends ByteMsgObjProcessor<CSHitMsg> {

    public CSHitProcessor() {
        super(CSHitMsg.class);
    }

    @Override
    public void process(Session session, MsgEvent<CSHitMsg> event) {

        RoomManager roomManager = getGameConsumer().getInstanceManager().getInstance(RoomManager.class);
        Player player = session.getSessionObject();

        Room room = roomManager.getRoom(player.getPlayerDB().getId());

        RoomPlayer roomPlayer = room.getRoomPlayerMap().get(player.getPlayerDB().getId());
        if(roomPlayer == null){
            return;
        }
        roomPlayer.setScore(roomPlayer.getScore() + 10);
//        roomPlayer.setHp(roomPlayer.getHp() + 1);



        String killMsg = roomPlayer.getPlayer().getPlayerDB().getName() + " Kill > ";

        RoomPlayer hitRoomPlayer = room.getRoomPlayerMap().get(event.getMsgData().getHitTargetId());
        SCHitMsg scHitMsg = new SCHitMsg();
        if(hitRoomPlayer != null){
            hitRoomPlayer.setDeadCount(hitRoomPlayer.getDeadCount()+1);
            hitRoomPlayer.setState(1);
            hitRoomPlayer.setHp(0);

            roomPlayer.setPlayerSize(roomPlayer.getPlayerSize() + Math.min(Math.max((hitRoomPlayer.getPlayerSize() - RoomManager.ROOM_PLAYER_MIN_SIZE) / 2, 2) , RoomManager.ROOM_PLAYER_MAX_SIZE));

            PlayerFrameMsg playerFrameMsg = roomPlayer.getRoom().getPlayerFrameMsgMap().get(hitRoomPlayer.getPlayer().getPlayerDB().getId());
            if(playerFrameMsg != null){
                playerFrameMsg.setState(1);
            }

            scHitMsg.setHitTargetId(hitRoomPlayer.getPlayer().getPlayerDB().getId());
            scHitMsg.setTargetHp(hitRoomPlayer.getHp());

            killMsg += hitRoomPlayer.getPlayer().getPlayerDB().getName();
        }



        for(RoomPlayer otherPlayer : room.getRoomPlayerMap().values() ){
            otherPlayer.getPlayer().sendTip(killMsg);
        }




        scHitMsg.setScore(roomPlayer.getScore());
        scHitMsg.setAddScore(1);
        scHitMsg.setAttackPlayerId(player.getPlayerDB().getId());
        scHitMsg.setAttackPlayerHp(roomPlayer.getHp());
        scHitMsg.setTargetState(1);


        room.broadcast(scHitMsg);


        if(hitRoomPlayer != null){
            getGameConsumer().getTimerManager().addTimer(1 , 6000L , ()->{
                roomManager.revive(hitRoomPlayer);
            });

            getGameConsumer().getTimerManager().addTimer(5,  1000L, new ITimerHandler() {
                int reviveCountDown = 5;
                @Override
                public void onTime() {
                    hitRoomPlayer.getPlayer().sendTip("复活倒计时 : " + reviveCountDown );
                    reviveCountDown--;
                }
            } );
        }

        SCUpdatePlayerScoreMsg sendScoreMsg = new SCUpdatePlayerScoreMsg();
        sendScoreMsg.setPlayerId(player.getPlayerDB().getId());
        sendScoreMsg.setScore(roomPlayer.getScore());
        sendScoreMsg.setPlayerSize(roomPlayer.getPlayerSize());
//        session.writeMessage(sendScoreMsg);
        room.broadcast(sendScoreMsg);


    }
}
