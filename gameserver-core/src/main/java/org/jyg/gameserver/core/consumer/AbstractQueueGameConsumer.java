package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;

import java.util.Queue;

/**
 * create by jiayaoguang on 2020/5/1
 */
public abstract class AbstractQueueGameConsumer extends SingleThreadGameConsumer {

    public static final int DEFAULT_QUEUE_SIZE = 256 * 1024;

    private final Queue<EventData<?>> queue;

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

}
