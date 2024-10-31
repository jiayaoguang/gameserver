package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.ResultReturnEvent;
import org.jyg.gameserver.core.exception.RequestTimeoutException;
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

        if(requestId == 0){
            throw new IllegalArgumentException("requestId == 0");
        }

        this.requestId = requestId;
        this.gameConsumer = gameConsumer;
    }

    public long getRequestId() {
        return requestId;
    }


    public Object waitForResult() throws RequestTimeoutException {
        return waitForResult(2*1000L);
    }


    public Object waitForResult(long maxWaitMills) throws RequestTimeoutException {


        long timeoutNanoTime = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(maxWaitMills);

        int pollNullNum = 0;


        QueueConsumerThread queueConsumerThread = gameConsumer.getConsumerThread();


        for (;!gameConsumer.isStop();){

            for(AbstractThreadQueueGameConsumer oneGameConsumer : queueConsumerThread.queueGameConsumers ){
                if( timeoutNanoTime <=  System.nanoTime()){
                    throw new RequestTimeoutException("waitForResult timeout");
                }

                EventData<?> object = oneGameConsumer.pollEvent();
                if(object == null) {
                    pollNullNum++;

                    if (pollNullNum > 100) {
                        oneGameConsumer.getTimerManager().updateTimer();
                        pollNullNum = 0;
                        LockSupport.parkNanos(1000 * 1000L);
                    } else if (pollNullNum > 50) {
                        Thread.yield();
                    }

                    continue;
                }

                pollNullNum = 0;

                try {
                    oneGameConsumer.getTimerManager().updateTimer();
                    oneGameConsumer.onReceiveEvent(object);
                } catch (Exception e) {
                    Logs.DEFAULT_LOGGER.error("waitForResult make exception : " , e);
                }
                if(object.getEvent() instanceof ResultReturnEvent){
                    ResultReturnEvent resultReturnEvent = (ResultReturnEvent)object.getEvent();
                    if( resultReturnEvent.getReturnToConsumerRequestId() == requestId ){
                        return ((ResultReturnEvent)object.getEvent()).getData();
                    }
                }
            }

        }

        return null;
    }

}
