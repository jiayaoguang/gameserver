package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.util.Logs;

public abstract class AbstractEvent<A, B> implements Event<A,B> {


    private Event<A,B> eventLogic;

    public AbstractEvent() {
    }

    public AbstractEvent(Event<A, B> eventLogic) {
        this.eventLogic = eventLogic;
    }


    @Override
    public final void onEvent(A param1, B param2){
        if(eventLogic == null){
            Logs.DEFAULT_LOGGER.error("eventLogic == null");
            return;
        }
        eventLogic.onEvent(param1,param2);
    }

    public void setEventLogic(Event<A, B> eventLogic) {
        this.eventLogic = eventLogic;
    }
}
