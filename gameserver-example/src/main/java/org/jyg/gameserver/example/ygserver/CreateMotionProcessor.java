package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.example.ygserver.msg.CSCreateMotionMsg;
import org.jyg.gameserver.example.ygserver.msg.SCCreateMotionMsg;

/**
 * create by jiayaoguang at 2021/9/4
 */
public class CreateMotionProcessor extends ByteMsgObjProcessor<CSCreateMotionMsg> {


    @Override
    public void process(Session session, MsgEvent<CSCreateMotionMsg> eventData) {

        CSCreateMotionMsg msg = eventData.getMsgData();

        Player player = session.getSessionObject();





        RoomManager roomManager = getGameConsumer().getInstance(RoomManager.class);

        Room room = roomManager.getRoom(player.getPlayerDB().getId());

        RoomPlayer roomPlayer = room.getRoomPlayerMap().get(player.getPlayerDB().getId());

        Motion motion = room.createMotion(roomPlayer  ,msg.getPosi(), msg.getType() );

        SCCreateMotionMsg sendMsg = new SCCreateMotionMsg();
        sendMsg.setMotionMsg(room.createMotionMsg(roomPlayer , motion));

        session.writeMessage(sendMsg);


    }
}
