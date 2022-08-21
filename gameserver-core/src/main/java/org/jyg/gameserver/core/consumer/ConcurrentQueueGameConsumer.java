package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * create by jiayaoguang on 2021/5/13
 */
public class ConcurrentQueueGameConsumer extends AbstractQueueGameConsumer {

    private final ConcurrentLinkedQueue<EventData<Object>> queue;

    public ConcurrentQueueGameConsumer() {
        queue = new ConcurrentLinkedQueue<>();
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
