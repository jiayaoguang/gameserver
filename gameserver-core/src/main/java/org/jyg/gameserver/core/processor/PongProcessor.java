package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.msg.PongByteMsg;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2021/5/29
 */
public class PongProcessor extends ByteMsgObjProcessor<PongByteMsg> {

    public PongProcessor() {
        super(PongByteMsg.class);
    }

    @Override
    public void process(Session session, EventData<PongByteMsg> event) {

        int i = 0;
    }
}
