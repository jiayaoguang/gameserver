package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.msg.PingByteMsg;
import org.jyg.gameserver.core.msg.PongByteMsg;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2021/5/29
 */
public class PingProcessor extends ByteMsgObjProcessor<PingByteMsg> {

    public static final PongByteMsg pongByteMsg = new PongByteMsg();

    public PingProcessor() {
        super(PingByteMsg.class);
    }

    @Override
    public void process(Session session, MsgEvent<PingByteMsg> event) {

        session.writeMessage(pongByteMsg);

    }
}
