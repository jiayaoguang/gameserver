package org.jyg.gameserver.core.consumer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2023/4/8
 * 指定睡眠时间间隔的消费者线程
 */
public class FixParkIntervalQueueConsumerThread extends QueueConsumerThread {


    private long nextParkNanoTime;

    private final long parkIntervalNano;

    public FixParkIntervalQueueConsumerThread() {
        this(null);
    }

    public FixParkIntervalQueueConsumerThread(String name) {
        this(name , 10L);
    }
    public FixParkIntervalQueueConsumerThread(String name, long parkIntervalMill) {
        super(name);
        if(parkIntervalMill <=0 || parkIntervalMill > 10000L){
            throw new IllegalArgumentException("parkIntervalMill <=0 || parkIntervalMill > 10000L");
        }
        parkIntervalNano = TimeUnit.MILLISECONDS.toNanos(parkIntervalMill);
        nextParkNanoTime = System.nanoTime() + parkIntervalNano;
    }






    @Override
    protected void beforeUpdate() {

    }

    @Override
    protected void afterUpdateApplyWait() {
        long curNano = System.nanoTime();

        if(nextParkNanoTime > curNano){
            return;
        }

        LockSupport.parkNanos(1000*1000L);
        nextParkNanoTime = System.nanoTime() + parkIntervalNano;
    }


}
