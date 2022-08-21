package org.jyg.gameserver.core.consumer;

import io.netty.util.internal.shaded.org.jctools.queues.MpscUnboundedArrayQueue;
import org.jyg.gameserver.core.data.EventData;

import java.util.Queue;

/**
 * create by jiayaoguang on 2021/5/13
 */
public class MpscQueueGameConsumer extends AbstractQueueGameConsumer {

    private final Queue<EventData<Object>> queue;

    public MpscQueueGameConsumer() {
        queue = new MpscUnboundedArrayQueue<>(1024*64);
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
