package org.jyg.gameserver.core.consumer;

import io.netty.util.internal.shaded.org.jctools.queues.MpscUnboundedArrayQueue;
import org.jyg.gameserver.core.data.EventData;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * create by jiayaoguang on 2021/5/13
 */
public class MpscQueueConsumer extends AbstractQueueConsumer {

    private final Queue<EventData<Object>> queue;

    public MpscQueueConsumer() {
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
