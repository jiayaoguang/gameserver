package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.util.Logs;

import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2023/4/8
 */
public class PollMoreNullWaitQueueConsumerThread extends QueueConsumerThread {



    private static final int YIELD_NEED_POLL_NULL_NUM = 500;
    private static final int PARK_NEED_POLL_NULL_NUM = 600;




    public PollMoreNullWaitQueueConsumerThread() {
        this(null);
    }


    public PollMoreNullWaitQueueConsumerThread(String name) {
        super(name);
    }






    protected void runSingleConsumer(AbstractThreadQueueGameConsumer gameConsumer) {



        for (; ; ) {

            if (gameConsumer.isStop()) {
                break;
            }

            try {
                gameConsumer.update();
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (gameConsumer.getContinuePollNullNum() > PARK_NEED_POLL_NULL_NUM) {
                gameConsumer.clearContinuePollNullNum();
                LockSupport.parkNanos(1000 * 1000L);
            } else if (gameConsumer.getContinuePollNullNum() > YIELD_NEED_POLL_NULL_NUM) {
                Thread.yield();
            }
        }
    }



    protected void runAllConsumers() {


        for (; ; ) {
            int aliveConsumerNum = 0;

            int minContinuePollNullCountNum = Integer.MAX_VALUE;

            for (AbstractThreadQueueGameConsumer gameConsumer : queueGameConsumers) {
                if (gameConsumer.isStop()) {
                    continue;
                } else {
                    aliveConsumerNum++;
                }
                try {
                    gameConsumer.update();
                } catch (Exception e) {
                    Logs.CONSUMER.error("update make exception : ",e);
                }
                minContinuePollNullCountNum = Math.min(gameConsumer.getContinuePollNullNum() , minContinuePollNullCountNum) ;

            }


            if (aliveConsumerNum == 0) {
                break;
            }

            if (minContinuePollNullCountNum > PARK_NEED_POLL_NULL_NUM) {
                for (AbstractThreadQueueGameConsumer gameConsumer : queueGameConsumers) {
                    gameConsumer.clearContinuePollNullNum();
                }
                LockSupport.parkNanos(1000 * 1000L);
            } else if (minContinuePollNullCountNum > YIELD_NEED_POLL_NULL_NUM) {
                Thread.yield();
            }

        }
    }


    @Override
    protected void beforeUpdate(){
        //do nothing
    }

    @Override
    protected void afterUpdateApplyWait(){
        //do nothing
    }

}
