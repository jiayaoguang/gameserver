package org.jyg.gameserver.core.msg;

import org.jyg.gameserver.core.event.Event;

import java.util.Map;

/**
 * create by jiayaoguang on 2022/11/12
 */
public class ConsumerEventDataMsg implements ByteMsgObj{

    private int toConsumerId;


    private int eventId;


    private String childChooseId;


    private Event event;



    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }


    public String getChildChooseId() {
        return childChooseId;
    }

    public void setChildChooseId(String childChooseId) {
        this.childChooseId = childChooseId;
    }




    public int getToConsumerId() {
        return toConsumerId;
    }

    public void setToConsumerId(int toConsumerId) {
        this.toConsumerId = toConsumerId;
    }


    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

}
