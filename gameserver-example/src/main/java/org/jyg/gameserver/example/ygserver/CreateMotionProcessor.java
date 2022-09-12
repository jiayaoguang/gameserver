package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.example.ygserver.msg.CSCreateMotionMsg;
import org.jyg.gameserver.example.ygserver.msg.ChatReplyJson;
import org.jyg.gameserver.example.ygserver.msg.ChatRequestJson;
import org.jyg.gameserver.example.ygserver.msg.SCCreateMotionMsg;

/**
 * create by jiayaoguang at 2021/9/4
 */
public class CreateMotionProcessor extends ByteMsgObjProcessor<CSCreateMotionMsg> {


    @Override
    public void process(Session session, EventData<CSCreateMotionMsg> eventData) {

        CSCreateMotionMsg msg = eventData.getData();

        Player player = session.getSessionObject();


        RoomManager roomManager = getGameConsumer().getInstance(RoomManager.class);
        RoomPlayer roomPlayer = roomManager.getRoom().getRoomPlayerMap().get(player.getPlayerDB().getId());

        Motion motion = roomManager.getRoom().createMotion(roomPlayer  ,msg.getPosi(), msg.getType() );

        SCCreateMotionMsg sendMsg = new SCCreateMotionMsg();
        sendMsg.setMotionMsg(roomManager.getRoom().createMotionMsg(roomPlayer , motion));

        session.writeMessage(sendMsg);


    }
}
