package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;

import java.util.concurrent.*;

/**
 * create by jiayaoguang on 2020/5/1
 */
public class BlockingQueueGameConsumer extends AbstractThreadQueueGameConsumer {


    public BlockingQueueGameConsumer() {
        this(new LinkedBlockingQueue<>(DEFAULT_QUEUE_SIZE));
    }

    public BlockingQueueGameConsumer(int size) {
        this(new LinkedBlockingQueue<>(size));
    }

    public BlockingQueueGameConsumer(BlockingQueue<EventData<?>> queue) {
        super(queue);
    }

}
