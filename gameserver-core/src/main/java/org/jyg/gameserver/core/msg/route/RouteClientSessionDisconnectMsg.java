package org.jyg.gameserver.core.msg.route;

import io.protostuff.Tag;
import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang on 2022/8/7
 */
public class RouteClientSessionDisconnectMsg implements ByteMsgObj {

    @Tag(1)
    private long sessionId;


    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

}
