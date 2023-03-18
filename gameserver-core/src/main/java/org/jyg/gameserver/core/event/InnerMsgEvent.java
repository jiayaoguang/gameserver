package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.data.EventData;

/**
 * create by jiayaoguang on 2023/2/26
 */
public class InnerMsgEvent implements Event {


    private final EventData<?> eventData;

    public InnerMsgEvent( EventData<?> eventData) {
        this.eventData = eventData;
    }

    public EventData<?> getEventData() {
        return eventData;
    }
}
