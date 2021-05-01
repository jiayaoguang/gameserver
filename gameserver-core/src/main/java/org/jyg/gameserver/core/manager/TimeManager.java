package org.jyg.gameserver.core.manager;

/**
 * create by jiayaoguang on 2021/5/1
 */
public class TimeManager implements Lifecycle{

    private volatile long lastTime;

    public TimeManager() {
        lastTime = System.currentTimeMillis();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }


    public long getTime(){

        long now = System.currentTimeMillis();
        if(now < lastTime){
            return lastTime;
        }

        lastTime = now;

        return now;
    }

}
