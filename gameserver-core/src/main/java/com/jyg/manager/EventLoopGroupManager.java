package com.jyg.manager;

import com.jyg.util.PrefixNameThreadFactory;
import com.jyg.util.RemotingUtil;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * create by jiayaoguang on 2020/5/4
 */
public class EventLoopGroupManager {

    private final EventLoopGroup bossGroup;

    protected final EventLoopGroup workGroup;

    public EventLoopGroupManager() {
        this.bossGroup = new NioEventLoopGroup(1,
                (Runnable r) -> new Thread(r, "ACCEPT_THREAD"));

        int defaultIOThreadNum = Runtime.getRuntime().availableProcessors() * 2;
        if (RemotingUtil.useEpoll()) {
            workGroup = new EpollEventLoopGroup(defaultIOThreadNum, new PrefixNameThreadFactory("EPOLL_IO_THREAD_"));
        } else {
            workGroup = new NioEventLoopGroup(defaultIOThreadNum, new PrefixNameThreadFactory("NIO_IO_THREAD_"));
        }
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public EventLoopGroup getWorkGroup() {
        return workGroup;
    }

    public void stopAllEventLoop(){
        this.bossGroup.shutdownGracefully();
        this.workGroup.shutdownGracefully();
    }
}
