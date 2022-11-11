package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * create by jiayaoguang on 2021/5/13
 */
public class ConcurrentQueueGameConsumer extends AbstractQueueGameConsumer {

    public ConcurrentQueueGameConsumer() {
        super(new ConcurrentLinkedQueue<>());
    }

}
