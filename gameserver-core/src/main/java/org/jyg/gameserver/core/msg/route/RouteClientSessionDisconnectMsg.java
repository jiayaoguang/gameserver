package org.jyg.gameserver.core.msg.route;

import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang on 2022/8/7
 */
public class RouteClientSessionDisconnectMsg implements ByteMsgObj {


    private long sessionId;


    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

}
