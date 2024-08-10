package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;

import java.util.Queue;

/**
 * create by jiayaoguang on 2024/8/8
 */
public class YieldQueueConsumerThread extends QueueConsumerThread{

    private int updateCount = 0;

    public YieldQueueConsumerThread() {

    }


    public YieldQueueConsumerThread(String name) {
        super(name);
    }


    @Override
    protected void beforeUpdate() {
        //do nothing
    }

    @Override
    protected void afterUpdateApplyWait() {
        updateCount ++;

        if( ( updateCount & 0xffff) == 0 ){
            Thread.yield();
        }

    }
}
