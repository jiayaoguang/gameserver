package org.jyg.gameserver.core.event;

/**
 * create by jiayaoguang on 2022/12/4
 * 整点时间
 */
public class HourEvent extends Event{


    private final long triggerTime;
    private final int hour;


    public HourEvent(long triggerTime, int hour) {
        this.triggerTime = triggerTime;
        this.hour = hour;
    }


    public long getTriggerTime() {
        return triggerTime;
    }

    public int getHour() {
        return hour;
    }

}
