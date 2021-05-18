package org.jyg.gameserver.core.data;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class EventExtData {

    public final int fromConsumerId;
    public final int requestId;
    public final long sessionId;


    public EventExtData(int fromConsumerId, int requestId, long sessionId) {
        this.fromConsumerId = fromConsumerId;
        this.requestId = requestId;
        this.sessionId = sessionId;
    }
}
