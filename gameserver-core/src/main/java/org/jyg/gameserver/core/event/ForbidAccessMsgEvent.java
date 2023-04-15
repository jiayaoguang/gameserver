package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2023/4/15
 */
public class ForbidAccessMsgEvent extends Event {


    private final Session session;

    private final Object msgData;


    public ForbidAccessMsgEvent(Session session, Object msgData) {
        this.session = session;
        this.msgData = msgData;
    }


    public Session getSession() {
        return session;
    }

    public Object getMsgData() {
        return msgData;
    }

}
