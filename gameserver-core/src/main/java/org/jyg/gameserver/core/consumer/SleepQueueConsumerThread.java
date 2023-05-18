package org.jyg.gameserver.core.consumer;

/**
 * create by jiayaoguang on 2023/4/8
 */
public class SleepQueueConsumerThread extends QueueConsumerThread {





    public SleepQueueConsumerThread() {
        this(null);
    }


    public SleepQueueConsumerThread(String name) {
        super(name);
    }






    @Override
    protected void beforeUpdate(){
        //do nothing
    }

    @Override
    protected void afterUpdateApplyWait(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException ignore) {
            //ignore
        }
    }

}
