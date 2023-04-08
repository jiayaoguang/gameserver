package org.jyg.gameserver.core.consumer;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * create by jiayaoguang on 2021/5/13
 */
public class ConcurrentQueueGameConsumer extends AbstractThreadQueueGameConsumer {

    public ConcurrentQueueGameConsumer() {
        super(new ConcurrentLinkedQueue<>());
    }

}
