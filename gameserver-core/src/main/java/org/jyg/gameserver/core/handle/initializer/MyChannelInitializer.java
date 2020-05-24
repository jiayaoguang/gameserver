package org.jyg.gameserver.core.handle.initializer;

import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.consumer.Consumer;
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

    public Consumer getGlobalQueue() {
        return context.getGlobalQueue();
    }

    public Context getContext() {
        return context;
    }


//    @Override
//    protected void initChannel(Channel channel) throws Exception {
//
//    }
}
