package com.jyg.net;

import com.jyg.handle.initializer.WebSocketServerInitializer;
import com.jyg.util.GlobalQueue;
import com.jyg.util.RemotingUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * create by jiayaoguang at 2018年3月6日
 */

public abstract class TcpService implements Service{
    protected static final EventLoopGroup bossGroup = new NioEventLoopGroup(1,
            (Runnable r) -> new Thread(r, "ACCEPT_THREAD"));

    protected static final EventLoopGroup workGroup;

    static {
        if (RemotingUtil.useEpoll()) {

            workGroup = new EpollEventLoopGroup(8, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "EPOLL_IO_THREAD_" + threadIndex.incrementAndGet());
                }
            });
        } else {
            workGroup = new NioEventLoopGroup(8, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "NIO_IO_THREAD_" + threadIndex.incrementAndGet());
                }
            });
        }
    }

    private ChannelInitializer<Channel> initializer = new WebSocketServerInitializer();

    private final int port;

    public TcpService(int port, ChannelInitializer<Channel> initializer){
        if (port < 0) {
            throw new IllegalArgumentException("port number cannot be negative ");
        }
        this.port = port;
        this.initializer = initializer;
    }

    public ChannelInitializer<Channel> getInitializer() {
        return initializer;
    }

    public void setInitializer(ChannelInitializer<Channel> initializer) {
        this.initializer = initializer;
    }

    /**
     * 启动端口监听方法
     *
     * @return
     * @throws Exception
     */
    public final void start() throws InterruptedException {

        if (initializer == null) {
//            throw new Exception("initializer must is not null");
        }

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workGroup);

        bootstrap.channel(RemotingUtil.useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        bootstrap.childHandler(initializer);

        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        // tcp等待三次握手队列的长度
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);// maybe useless
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        // 指定等待时间为0，此时调用主动关闭时不会发送FIN来结束连接，而是直接将连接设置为CLOSE状态，
        // 清除套接字中的发送和接收缓冲区，直接对对端发送RST包。
        bootstrap.childOption(ChannelOption.SO_LINGER, 0);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 64 * 1024);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 64 * 1024);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);// maybe useless
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        bootstrap.bind(port).sync().channel();
        System.out.println("正在开启端口监听，端口号 :" + port);
    }
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
