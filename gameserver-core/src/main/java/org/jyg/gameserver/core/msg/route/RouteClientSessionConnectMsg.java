package org.jyg.gameserver.core.msg.route;

import io.protostuff.Tag;
import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang on 2022/8/7
 */
public class RouteClientSessionConnectMsg implements ByteMsgObj {


    @Tag(1)
    private long sessionId;
    @Tag(2)
    private String addr;


    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
