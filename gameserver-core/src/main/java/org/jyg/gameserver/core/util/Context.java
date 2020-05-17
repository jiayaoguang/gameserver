package org.jyg.gameserver.core.util;

import org.jyg.gameserver.core.manager.EventLoopGroupManager;
import org.jyg.gameserver.core.net.EventDispatcher;

/**
 * create by jiayaoguang on 2020/5/3
 */
public class Context {

    private final IGlobalQueue globalQueue;
    private final EventDispatcher eventDispatcher;
    private final EventLoopGroupManager eventLoopGroupManager;
//    private final ExecutorManager executorManager;

    public Context(IGlobalQueue globalQueue) {
        this.globalQueue = globalQueue;
        this.eventLoopGroupManager = new EventLoopGroupManager();
//        this.executorManager = new ExecutorManager(10, globalQueue);
        this.eventDispatcher = new EventDispatcher(globalQueue);
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
