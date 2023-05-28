package org.jyg.gameserver.core.msg.route;

import io.protostuff.Tag;
import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang on 2022/8/7
 */
public class RouteMsg implements ByteMsgObj {

    @Tag(1)
    private int msgId;
    @Tag(2)
    private long sessionId;
    @Tag(3)
    private byte[] data;


    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
