package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.util.Logs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2023/4/8
 * 指定循环时间间隔的消费者线程
 * 实际执行的时间间隔会大于等于传入的时间间隔
 */
public class IntervalUpdateQueueConsumerThread extends QueueConsumerThread {


    private long lastUpdateNanoTime;

    private final long updateIntervalNano;

    public IntervalUpdateQueueConsumerThread() {
        this(null);
    }

    public IntervalUpdateQueueConsumerThread(String name) {
        this(name , 2L);
    }
    public IntervalUpdateQueueConsumerThread(String name,long updateIntervalMill) {
        super(name);
        updateIntervalNano = TimeUnit.MILLISECONDS.toNanos(updateIntervalMill);
        lastUpdateNanoTime = System.nanoTime() + updateIntervalNano;
    }




    @Override
    protected void beforeUpdate() {


    }

    @Override
    protected void afterUpdateApplyWait() {
        long curNano = System.nanoTime();
        long nextUpdateNano = lastUpdateNanoTime + updateIntervalNano;


        if(nextUpdateNano > curNano){
            LockSupport.parkNanos(nextUpdateNano - curNano);
        }else {
            Thread.yield();
        }
        lastUpdateNanoTime = System.nanoTime();
    }


}
