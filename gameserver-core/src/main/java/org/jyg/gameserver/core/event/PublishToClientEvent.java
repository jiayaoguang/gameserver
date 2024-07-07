package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.data.EventData;

/**
 * create by jiayaoguang on 2024/7/7
 */
public class PublishToClientEvent extends Event {

    private EventData<?> eventData;

    private int targetConsumerId;

    public PublishToClientEvent(EventData<?> eventData,int targetConsumerId){
        this.eventData = eventData;
        this.targetConsumerId = targetConsumerId;
    }


    public EventData<?> getEventData(){
        return eventData;
    }

    public int getTargetConsumerId(){
        return targetConsumerId;
    }
}
