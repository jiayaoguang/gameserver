package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.util.Logs;

import javax.naming.OperationNotSupportedException;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2023/4/8
 */
public class PollMoreNullWaitQueueConsumerThread extends QueueConsumerThread {



    private final int yieldNeedPollNullNum = 50;
    private final int parkNeedPollNullNum = 100;




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


            if (gameConsumer.getContinuePollNullNum() > parkNeedPollNullNum) {
                gameConsumer.clearContinuePollNullNum();
                LockSupport.parkNanos(1000 * 1000L);
            } else if (gameConsumer.getContinuePollNullNum() > yieldNeedPollNullNum) {
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

            if (minContinuePollNullCountNum > parkNeedPollNullNum) {
                for (AbstractThreadQueueGameConsumer gameConsumer : queueGameConsumers) {
                    gameConsumer.clearContinuePollNullNum();
                }
                LockSupport.parkNanos(1000 * 1000L);
            } else if (minContinuePollNullCountNum > yieldNeedPollNullNum) {
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
