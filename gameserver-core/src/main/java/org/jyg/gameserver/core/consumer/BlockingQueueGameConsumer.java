package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;

import java.util.concurrent.*;

/**
 * create by jiayaoguang on 2020/5/1
 */
public class BlockingQueueGameConsumer extends AbstractQueueGameConsumer {

    private final BlockingQueue<EventData<Object>> queue;

    public BlockingQueueGameConsumer() {
        this(new LinkedBlockingQueue<>());
    }

    public BlockingQueueGameConsumer(int size) {
        this(new LinkedBlockingQueue<>(size));
    }

    public BlockingQueueGameConsumer(BlockingQueue<EventData<Object>> queue) {
        this.queue = queue;
    }

    @Override
    protected EventData<Object> pollEvent(){
        return queue.poll();
    }

    @Override
    protected void publicEvent(EventData<Object> eventData) {
        queue.offer(eventData);
    }

}
