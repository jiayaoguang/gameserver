package com.jyg.net;

import com.jyg.handle.initializer.InnerSocketServerInitializer;
import com.jyg.handle.initializer.MyChannelInitializer;
import com.jyg.util.IGlobalQueue;
import com.jyg.util.PrefixNameThreadFactory;
import com.jyg.util.RemotingUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * Created by jiayaoguang on 2018/7/6.
 */
public class UdpService extends AbstractService {

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

    private final int port;


    public UdpService(int port, IGlobalQueue globalQueue) {
        super(globalQueue);
        if (port < 0) {
            throw new IllegalArgumentException("port number cannot be negative ");
        }
        this.port = port;
    }

    @Override
    public void start() throws InterruptedException {

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(workGroup);

        bootstrap.channel(RemotingUtil.useEpoll() ? EpollDatagramChannel.class : NioDatagramChannel.class);
        bootstrap.handler(new InnerSocketServerInitializer(globalQueue));
//        bootstrap.childHandler(initializer);
        bootstrap.option(ChannelOption.SO_BROADCAST, true);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);


        serverChannel = bootstrap.bind(port).sync().channel();

        System.out.print("bind port : " + port);
    }

    public Channel getChannel() {
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
    }

}
