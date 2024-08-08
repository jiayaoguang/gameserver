package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.msg.GetServerTimeRequestMsg;
import org.jyg.gameserver.core.msg.GetServerTimeResponseMsg;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2024/8/8
 */
public class GetServerTimeProcessor extends ByteMsgObjProcessor<GetServerTimeRequestMsg>{
    @Override
    public void process(Session session, MsgEvent<GetServerTimeRequestMsg> event) {
        GetServerTimeResponseMsg getServerTimeResponseMsg = new GetServerTimeResponseMsg();
        getServerTimeResponseMsg.setTime(System.currentTimeMillis());

        session.writeMessage(getServerTimeResponseMsg);
    }
}
