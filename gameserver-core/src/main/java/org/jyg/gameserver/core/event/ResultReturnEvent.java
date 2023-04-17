package org.jyg.gameserver.core.event;

public class ResultReturnEvent extends Event {

    private final long returnToConsumerRequestId;
    private final int eventId;
    private final Object data;

    public ResultReturnEvent(long returnToConsumerRequestId, int eventId, Object data ) {
//        setRequestId(requestId);
        this.returnToConsumerRequestId = returnToConsumerRequestId;
        this.eventId = eventId;
        this.data = data;
        setFromConsumerId(0);
    }


    public ResultReturnEvent(long returnToConsumerRequestId, int eventId, Object data , int retuenFromConsumerId) {
//        setRequestId(requestId);
        this.returnToConsumerRequestId = returnToConsumerRequestId;
        this.eventId = eventId;
        this.data = data;
        setFromConsumerId(retuenFromConsumerId);
    }


    public int getEventId() {
        return eventId;
    }

    public Object getData() {
        return data;
    }


    public long getReturnToConsumerRequestId() {
        return returnToConsumerRequestId;
    }
}
