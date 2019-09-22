package com.jyg.net;

import com.jyg.handle.initializer.InnerSocketServerInitializer;
import com.jyg.util.GlobalQueue;
import com.jyg.util.PrefixNameThreadFactory;
import com.jyg.util.RemotingUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jiayaoguang on 2018/7/6.
 */
public class UdpService implements Service {

    protected static final EventLoopGroup bossGroup = new NioEventLoopGroup(1,
            (Runnable r) -> new Thread(r, "ACCEPT_THREAD"));

    protected static final EventLoopGroup workGroup;

    private Channel serverChannel;

    static {
        int defaultIOThreadNum = Runtime.getRuntime().availableProcessors() * 2;
        if (RemotingUtil.useEpoll()) {
            workGroup = new EpollEventLoopGroup(defaultIOThreadNum, new PrefixNameThreadFactory("EPOLL_IO_THREAD_"));
        } else {
            workGroup = new NioEventLoopGroup(defaultIOThreadNum, new PrefixNameThreadFactory("NIO_IO_THREAD_"));
        }
    }

    private ChannelInitializer<Channel> initializer = new InnerSocketServerInitializer();
    private final int port;



    public UdpService(int port, ChannelInitializer<Channel> initializer){
        if (port < 0) {
            throw new IllegalArgumentException("port number cannot be negative ");
        }
        this.port = port;
        this.initializer = initializer;
    }

    @Override
    public void start() throws InterruptedException {
        if (initializer == null) {
            throw new IllegalArgumentException("initializer must is not null");
        }

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group( workGroup);

        bootstrap.channel(RemotingUtil.useEpoll() ? EpollDatagramChannel.class : NioDatagramChannel.class);
        bootstrap.handler(initializer);
//        bootstrap.childHandler(initializer);
        bootstrap.option(ChannelOption.SO_BROADCAST, true);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);


        serverChannel = bootstrap.bind(port).sync().channel();

        System.out.print("bind port : " + port);
    }

    public Channel getChannel(){
        return serverChannel;
    }

    @Override
    public void stop() {

    }

    /**
     * 停止服务
     */
    public static void shutdown() {
        if (bossGroup != null && !bossGroup.isShutdown()) {
            bossGroup.shutdownGracefully();
        }
        if (workGroup != null && !workGroup.isShutdown()) {
            workGroup.shutdownGracefully();
        }
        GlobalQueue.shutdown();
    }

}
