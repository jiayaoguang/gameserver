package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.util.Logs;

import java.util.Queue;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2020/5/1
 */
public abstract class AbstractQueueGameConsumer extends GameConsumer {

    /**
     * 从队列取出值为空的连续次数
     */
    private static final int MAX_CONTINUE_POLL_NULL_NUM = 100_000_000;
    public static final int DEFAULT_QUEUE_SIZE = 256 * 1024;


    private int continuePollNullNum = 0;


    private final Queue<EventData<?>> queue;


    private QueueConsumerThread consumerThread;


    /**
     * queue 必须是线程安全的队列
     */
    protected AbstractQueueGameConsumer(Queue<EventData<?>> queue) {
        this.queue = queue;
    }


    protected EventData<?> pollEvent(){
        return queue.poll();
    }

    @Override
    public void publicEvent(EventData<?> eventData) {
        queue.offer(eventData);
    }


    @Override
    public void doStart() {

        if(consumerThread == null){
            consumerThread = new QueueConsumerThread(this);
            consumerThread.setName(getClass().getSimpleName() + "_" + getId());
        }
        if(!consumerThread.isStart()){
            consumerThread.start();
        }

    }

    public QueueConsumerThread getConsumerThread() {
        return consumerThread;
    }

    public void setConsumerThread(QueueConsumerThread consumerThread) {
        this.consumerThread = consumerThread;
    }

    public int getContinuePollNullNum() {
        return continuePollNullNum;
    }

    public void clearContinuePollNullNum() {
        this.continuePollNullNum = 0;
    }


    @Override
    public void doStop() {
        if(consumerThread != null){
            for (int i = 0; consumerThread.isAlive(); i++) {
                if(i > 1000){
                    Logs.DEFAULT_LOGGER.error( "consumer {} consumerThread stop fail " , getId());
                    break;
                }

                LockSupport.parkNanos(10_1000_1000L);
            }
        }
        Logs.DEFAULT_LOGGER.info("stop gameConsumer {} id {} success....", this.getClass().getSimpleName() ,getId() );
    }



    protected void updateConsumer() {

        for(;;){
            EventData<?> object = pollEvent();
            if(object == null) {
                if(continuePollNullNum < MAX_CONTINUE_POLL_NULL_NUM){
                    continuePollNullNum++;
                }
                return;
            }

            continuePollNullNum = 0;
            try {
                onReciveEvent(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
