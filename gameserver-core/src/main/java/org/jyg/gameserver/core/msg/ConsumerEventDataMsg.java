package org.jyg.gameserver.core.msg;

import org.jyg.gameserver.core.enums.EventType;

import java.util.Map;

/**
 * create by jiayaoguang on 2022/11/12
 */
public class ConsumerEventDataMsg implements ByteMsgObj{

    private int toConsumerId;;

    private EventType eventType;

    private int eventId;

    private int fromConsumerId;
    private long requestId;

    private long childChooseId;


    private Object data;


    private Map<String, Object> params;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }


    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

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

    public long getChildChooseId() {
        return childChooseId;
    }

    public void setChildChooseId(long childChooseId) {
        this.childChooseId = childChooseId;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }


    public int getToConsumerId() {
        return toConsumerId;
    }

    public void setToConsumerId(int toConsumerId) {
        this.toConsumerId = toConsumerId;
    }
}
