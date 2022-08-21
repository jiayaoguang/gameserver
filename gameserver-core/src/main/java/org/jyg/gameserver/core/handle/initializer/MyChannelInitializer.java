package org.jyg.gameserver.core.handle.initializer;

import org.jyg.gameserver.core.util.GameContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * create by jiayaoguang on 2020/5/2
 */
public abstract class MyChannelInitializer<C extends Channel> extends ChannelInitializer<C> {

    protected final GameContext gameContext;

    protected MyChannelInitializer(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public GameContext getGameContext() {
        return gameContext;
    }


//    @Override
//    protected void initChannel(Channel channel) throws Exception {
//
//    }
}
