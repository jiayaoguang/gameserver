package com.jyg.util;

import com.jyg.net.EventDispatcher;

/**
 * create by jiayaoguang on 2020/5/3
 */
public class Context {

    private final IGlobalQueue globalQueue;
    private final EventDispatcher eventDispatcher;


    public Context(IGlobalQueue globalQueue) {
        this.globalQueue = globalQueue;
        eventDispatcher = new EventDispatcher();
    }


    public IGlobalQueue getGlobalQueue() {
        return globalQueue;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
}
