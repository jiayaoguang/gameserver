package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.example.ygserver.msg.*;

/**
 * create by jiayaoguang at 2021/9/4
 */
public class CSEatScoreMotionProcessor extends ByteMsgObjProcessor<CSEatScoreMotionMsg> {


    @Override
    public void process(Session session, EventData<CSEatScoreMotionMsg> eventData) {

        CSEatScoreMotionMsg msg = eventData.getData();

        Player player = session.getSessionObject();





        RoomManager roomManager = getGameConsumer().getInstance(RoomManager.class);

        Room room = roomManager.getRoom(player.getPlayerDB().getId());

        RoomPlayer roomPlayer = room.getRoomPlayerMap().get(player.getPlayerDB().getId());

        Motion motion = room.getSysMotionMap().get(msg.getMotionUid());
        if(motion == null){
            return;
        }

        room.getSysMotionMap().remove(motion.getId());
        roomPlayer.setScore(roomPlayer.getScore() + 1);

        SCMotionDeadMsg sendMsg = new SCMotionDeadMsg();
        sendMsg.setMotionUid(motion.getId());
//        session.writeMessage(sendMsg);
        room.broadcast(sendMsg);

        SCUpdatePlayerScoreMsg sendScoreMsg = new SCUpdatePlayerScoreMsg();
        sendScoreMsg.setPlayerId(player.getPlayerDB().getId());
        sendScoreMsg.setScore(roomPlayer.getScore());
        session.writeMessage(sendScoreMsg);

    }
}
