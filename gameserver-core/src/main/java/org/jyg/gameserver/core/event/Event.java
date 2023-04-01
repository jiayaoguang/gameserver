package org.jyg.gameserver.core.event;

import java.util.Map;

public abstract class Event {

    private int fromConsumerId;

    private long requestId;

    private Map<String, Object> params;

    public int getFromConsumerId() {
        return fromConsumerId;
    }

    public void setFromConsumerId(int fromConsumerId) {
        this.fromConsumerId = fromConsumerId;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }


    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
