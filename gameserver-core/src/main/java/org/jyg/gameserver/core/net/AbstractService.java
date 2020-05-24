package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.consumer.IGlobalQueue;

/**
 * create by jiayaoguang on 2020/5/2
 */
public abstract class AbstractService implements Service{

    protected final IGlobalQueue globalQueue;


    protected AbstractService(IGlobalQueue globalQueue) {
        this.globalQueue = globalQueue;
    }

    public IGlobalQueue getGlobalQueue() {
        return globalQueue;
    }

}
