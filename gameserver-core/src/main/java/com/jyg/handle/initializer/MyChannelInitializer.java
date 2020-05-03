package com.jyg.handle.initializer;

import com.jyg.util.Context;
import com.jyg.util.IGlobalQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * create by jiayaoguang on 2020/5/2
 */
public abstract class MyChannelInitializer<C extends Channel> extends ChannelInitializer<C> {

    protected final Context context;

    protected MyChannelInitializer(Context context) {
        this.context = context;
    }

    public IGlobalQueue getGlobalQueue() {
        return context.getGlobalQueue();
    }


//    @Override
//    protected void initChannel(Channel channel) throws Exception {
//
//    }
}
