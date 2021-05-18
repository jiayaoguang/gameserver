package org.jyg.gameserver.core.consumer;

import io.netty.channel.Channel;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.util.Logs;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2021/5/13
 */
public class ConcurrentQueueConsumer extends AbstractQueueConsumer {

    private final ConcurrentLinkedQueue<EventData<Object>> queue;

    public ConcurrentQueueConsumer() {
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
