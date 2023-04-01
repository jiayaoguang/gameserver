package org.jyg.gameserver.core.event;

public class ResultReturnEvent extends Event {

    private final int eventId;
    private final Object data;

    public ResultReturnEvent(long requestId, int eventId, Object data) {
        setRequestId(requestId);
        this.eventId = eventId;
        this.data = data;
    }


    public int getEventId() {
        return eventId;
    }

    public Object getData() {
        return data;
    }
}
