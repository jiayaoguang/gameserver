package com.jyg.handle.initializer;

import com.jyg.util.IGlobalQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * create by jiayaoguang on 2020/5/2
 */
public abstract class MyChannelInitializer<C extends Channel> extends ChannelInitializer<C> {

    protected final IGlobalQueue globalQueue;

    protected MyChannelInitializer(IGlobalQueue globalQueue) {
        this.globalQueue = globalQueue;
    }

    public IGlobalQueue getGlobalQueue() {
        return globalQueue;
    }


//    @Override
//    protected void initChannel(Channel channel) throws Exception {
//
//    }
}
