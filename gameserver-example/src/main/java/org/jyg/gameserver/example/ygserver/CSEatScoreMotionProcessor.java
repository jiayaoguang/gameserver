package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.example.ygserver.msg.*;

/**
 * create by jiayaoguang at 2021/9/4
 */
public class CSEatScoreMotionProcessor extends ByteMsgObjProcessor<CSEatScoreMotionMsg> {


    @Override
    public void process(Session session, MsgEvent<CSEatScoreMotionMsg> eventData) {

        CSEatScoreMotionMsg msg = eventData.getMsgData();

        Player player = session.getSessionObject();

        if(player == null){
            Logs.DEFAULT_LOGGER.error(" roomPlayer not found ");
            return;
        }



        RoomManager roomManager = getGameConsumer().getInstance(RoomManager.class);

        Room room = roomManager.getRoom(player.getPlayerDB().getId());

        RoomPlayer roomPlayer = room.getRoomPlayerMap().get(player.getPlayerDB().getId());

        if(roomPlayer == null){
            Logs.DEFAULT_LOGGER.error(" roomPlayer not found ");
            return;
        }

        Motion motion = room.getSysMotionMap().get(msg.getMotionUid());
        if(motion == null){
            return;
        }

        double distance = Math.sqrt( Math.pow(Math.abs( roomPlayer.getPosi().getX() - motion.getPosi().getX() ) , 2) + Math.pow(Math.abs( roomPlayer.getPosi().getY() - motion.getPosi().getY() ) , 2) );
        if(distance > (roomPlayer.getPlayerSize() + 5 )){
            Logs.DEFAULT_LOGGER.error("eat score fail , distance {} too big" , distance);
            return;
        }
        room.getSysMotionMap().remove(motion.getId());
        roomPlayer.setScore(roomPlayer.getScore() + 1);
        roomPlayer.setPlayerSize(Math.min(roomPlayer.getPlayerSize() + 0.5f , RoomManager.ROOM_PLAYER_MAX_SIZE));

        SCMotionDeadMsg sendMsg = new SCMotionDeadMsg();
        sendMsg.setMotionUid(motion.getId());
//        session.writeMessage(sendMsg);
        room.broadcast(sendMsg);

        SCUpdatePlayerScoreMsg sendScoreMsg = new SCUpdatePlayerScoreMsg();
        sendScoreMsg.setPlayerId(player.getPlayerDB().getId());
        sendScoreMsg.setScore(roomPlayer.getScore());
        sendScoreMsg.setPlayerSize(roomPlayer.getPlayerSize());
//        session.writeMessage(sendScoreMsg);
        room.broadcast(sendScoreMsg);

    }
}
