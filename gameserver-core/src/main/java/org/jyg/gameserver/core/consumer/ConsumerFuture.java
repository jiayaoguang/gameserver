package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.enums.EventType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2022/10/29
 */
public class ConsumerFuture {

    private final long requestId;

    private final AbstractQueueGameConsumer gameConsumer;

    public ConsumerFuture(long requestId, AbstractQueueGameConsumer gameConsumer) {
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
                throw new RuntimeException("waitFOrResult timeout");
            }

            EventData<Object> object = gameConsumer.pollEvent();
            if(object == null) {
                pollNullNum++;

                if (pollNullNum > 1000) {
                    gameConsumer.update();
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            if( object.getEventExtData().requestId == requestId && object.getEventType() == EventType.RESULT_CALL_BACK){
                return object.getData();
            }
        }

        return null;
    }

}
