package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.ResultReturnEvent;
import org.jyg.gameserver.core.util.Logs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2022/10/29
 */
public class ConsumerFuture {

    private final long requestId;

    private final AbstractThreadQueueGameConsumer gameConsumer;

    public ConsumerFuture(long requestId, AbstractThreadQueueGameConsumer gameConsumer) {
        this.requestId = requestId;
        this.gameConsumer = gameConsumer;
    }

    public long getRequestId() {
        return requestId;
    }


    public Object waitForResult(){
        return waitForResult(20*1000L);
    }


    public Object waitForResult(long maxWaitMills){


        long timeoutNanoTime = System.nanoTime() + TimeUnit.MINUTES.toNanos(maxWaitMills);

        int pollNullNum = 0;

        for (;!gameConsumer.isStop();){


            if( timeoutNanoTime <=  System.nanoTime()){
                throw new RuntimeException("waitForResult timeout");
            }

            EventData<?> object = gameConsumer.pollEvent();
            if(object == null) {
                pollNullNum++;

                if (pollNullNum > 1000) {
                    gameConsumer.getTimerManager().updateTimer();
                    pollNullNum = 0;
                    LockSupport.parkNanos(1000 * 1000L);
                } else if (pollNullNum > 800) {
                    Thread.yield();
                }

                continue;
            }

            pollNullNum = 0;

            try {
                gameConsumer.onReciveEvent(object);
                gameConsumer.getTimerManager().updateTimer();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if( object.getEvent().getRequestId() == requestId ){

                if(object.getEvent() instanceof ResultReturnEvent){
                    return ((ResultReturnEvent)object.getEvent()).getData();
                }else {
                    Logs.DEFAULT_LOGGER.error("return object.getEvent() not instanceof ResultReturnEvent");
                    return null;
                }
            }
        }

        return null;
    }

}
