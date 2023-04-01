package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.session.Session;

public class MQMsgEvent extends MsgEvent<Object>{

    private final Session mqSession;

    public MQMsgEvent(int msgId, Object data, Session mqSession) {
        super(msgId, data);
        this.mqSession = mqSession;
    }

    public Session getMqSession() {
        return mqSession;
    }
}
