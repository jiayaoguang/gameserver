package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.util.PrefixNameThreadFactory;
import org.jyg.gameserver.core.util.RemotingUtil;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * create by jiayaoguang on 2020/5/4
 */
public class EventLoopGroupManager {

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workGroup;

    public EventLoopGroupManager(boolean useEpoll) {
        this(useEpoll,Runtime.getRuntime().availableProcessors() * 2);
    }

    public EventLoopGroupManager(boolean useEpoll, int selectorThreadNum) {
        if (useEpoll) {
            this.bossGroup = new EpollEventLoopGroup(1, new PrefixNameThreadFactory("NettyEpollBossThread_"));
            this.workGroup = new EpollEventLoopGroup(selectorThreadNum, new PrefixNameThreadFactory("NettyEpollWorkThread_"));
        } else {
            this.bossGroup = new NioEventLoopGroup(1, new PrefixNameThreadFactory("NettyNioBossThread_"));
            this.workGroup = new NioEventLoopGroup(selectorThreadNum, new PrefixNameThreadFactory("NettyNioWorkThread_"));
        }
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public EventLoopGroup getWorkGroup() {
        return workGroup;
    }

    public void stopAllEventLoop() {
        this.bossGroup.shutdownGracefully();
        this.workGroup.shutdownGracefully();
    }
}
