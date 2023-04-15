package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.util.Logs;

import java.util.Queue;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2020/5/1
 * 单线程队列消费者
 */
public abstract class AbstractThreadQueueGameConsumer extends GameConsumer {

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
    protected AbstractThreadQueueGameConsumer(Queue<EventData<?>> queue) {
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
            consumerThread = new QueueConsumerThread();
            consumerThread.setName(getClass().getSimpleName() + "_" + getId());
        }
        consumerThread.addQueueConsumer(this);
        if(!consumerThread.isStart()){
            consumerThread.start();
        }

    }

    public QueueConsumerThread getConsumerThread() {
        return consumerThread;
    }

    public void setConsumerThreadAndAddToThread(QueueConsumerThread consumerThread) {
        if(isStart()){
            throw new IllegalStateException("isStart , setConsumerThread fail");
        }
        if(this.consumerThread != null){
            throw new IllegalArgumentException("this.consumerThread != null , setConsumerThread fail");
        }
        this.consumerThread = consumerThread;
        this.consumerThread.addQueueConsumer(this);
    }

    public int getContinuePollNullNum() {
        return continuePollNullNum;
    }

    public void clearContinuePollNullNum() {
        this.continuePollNullNum = 0;
    }


    @Override
    public void doStop() {
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
