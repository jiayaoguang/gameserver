package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.consumer.Consumer;

/**
 * create by jiayaoguang on 2020/5/2
 */
public abstract class AbstractService implements Service{

    protected final Consumer globalQueue;


    protected AbstractService(Consumer globalQueue) {
        this.globalQueue = globalQueue;
    }

    public Consumer getGlobalQueue() {
        return globalQueue;
    }

}
