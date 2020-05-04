package com.jyg.util;

import com.jyg.manager.EventLoopGroupManager;
import com.jyg.net.EventDispatcher;

/**
 * create by jiayaoguang on 2020/5/3
 */
public class Context {

    private final IGlobalQueue globalQueue;
    private final EventDispatcher eventDispatcher;
    private final EventLoopGroupManager eventLoopGroupManager;

    public Context(IGlobalQueue globalQueue) {
        this.globalQueue = globalQueue;
        this.eventDispatcher = new EventDispatcher();
        this.eventLoopGroupManager = new EventLoopGroupManager();
    }


    public IGlobalQueue getGlobalQueue() {
        return globalQueue;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public EventLoopGroupManager getEventLoopGroupManager() {
        return eventLoopGroupManager;
    }
}
