package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.util.PrefixNameThreadFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * create by jiayaoguang on 2020/5/4
 */
public class EventLoopGroupManager implements Lifecycle{

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workGroup;

    public EventLoopGroupManager(boolean useEpoll) {
        this(useEpoll, Math.min(Runtime.getRuntime().availableProcessors() + 1, 32));
    }

    public EventLoopGroupManager(boolean useEpoll, int nettyIOThreadNum) {
        if (useEpoll) {
            this.bossGroup = new EpollEventLoopGroup(1, new PrefixNameThreadFactory("NettyEpollBossThread_"));
            this.workGroup = new EpollEventLoopGroup(nettyIOThreadNum, new PrefixNameThreadFactory("NettyEpollWorkThread_"));
        } else {
            this.bossGroup = new NioEventLoopGroup(1, new PrefixNameThreadFactory("NettyNioBossThread_"));
            this.workGroup = new NioEventLoopGroup(nettyIOThreadNum, new PrefixNameThreadFactory("NettyNioWorkThread_"));
        }
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public EventLoopGroup getWorkGroup() {
        return workGroup;
    }


    @Override
    public void start() {
        //do nothing
    }

    @Override
    public void stop() {
        this.bossGroup.shutdownGracefully();
        this.workGroup.shutdownGracefully();
    }
}
