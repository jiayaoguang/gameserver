package org.jyg.gameserver.core.event;

import java.util.Map;

public class ConsumerDefaultEvent extends Event {

    private int eventId;

    private Object data;


    public ConsumerDefaultEvent() {
    }

    public ConsumerDefaultEvent(int eventId, Object data) {
        this.eventId = eventId;
        this.data = data;
    }



    public ConsumerDefaultEvent(int eventId, Object data, long requestId, int fromConsumerId, Map<String, Object> params) {
        this.eventId = eventId;
        setRequestId(requestId);
        this.data = data;
        setParams(params);
        setFromConsumerId(fromConsumerId);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }



}
