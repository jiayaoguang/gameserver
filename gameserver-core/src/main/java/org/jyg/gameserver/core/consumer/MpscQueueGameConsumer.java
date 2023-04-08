package org.jyg.gameserver.core.consumer;

import io.netty.util.internal.shaded.org.jctools.queues.MpscUnboundedArrayQueue;

/**
 * create by jiayaoguang on 2021/5/13
 */
public class MpscQueueGameConsumer extends AbstractThreadQueueGameConsumer {

    public MpscQueueGameConsumer() {
        this(DEFAULT_QUEUE_SIZE);
    }

    public MpscQueueGameConsumer(int size) {
        super(new MpscUnboundedArrayQueue<>(size));
    }

}
