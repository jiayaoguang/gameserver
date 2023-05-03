package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2023/5/3
 */
public class DisableAccessMsgEvent extends Event {


    private final Session session;

    private final Object msgData;


    public DisableAccessMsgEvent(Session session, Object msgData) {
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
