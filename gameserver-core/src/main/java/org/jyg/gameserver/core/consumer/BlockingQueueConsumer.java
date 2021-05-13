package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.enums.EventType;
import io.netty.channel.Channel;
import org.jyg.gameserver.core.util.Logs;

import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2020/5/1
 */
public class BlockingQueueConsumer extends AbstractQueueConsumer {

    private final BlockingQueue<EventData<Object>> queue;

    public BlockingQueueConsumer() {
        this(new LinkedBlockingQueue<>());
    }

    public BlockingQueueConsumer(int size) {
        this(new LinkedBlockingQueue<>(size));
    }

    public BlockingQueueConsumer(BlockingQueue<EventData<Object>> queue) {
        this.queue = queue;
    }

    @Override
    protected EventData<Object> pollEvent(){
        return queue.poll();
    }

    @Override
    protected void offerEvent(EventData<Object> eventData) {
        queue.offer(eventData);
    }

}
