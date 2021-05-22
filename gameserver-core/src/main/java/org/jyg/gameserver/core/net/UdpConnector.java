package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.handle.initializer.InnerSocketServerInitializer;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.Logs;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * Created by jiayaoguang on 2018/7/6.
 */
public class UdpConnector extends AbstractConnector {

    protected final EventLoopGroup bossGroup ;

    protected final EventLoopGroup workGroup;

    private Channel serverChannel;

    private final int port;

    private final Context context;

    public UdpConnector(int port, Context context) {
        super(context);
        if (port < 0) {
            throw new IllegalArgumentException("port number cannot be negative ");
        }
        this.port = port;
        this.context = context;
        this.workGroup = context.getEventLoopGroupManager().getWorkGroup();
        this.bossGroup = context.getEventLoopGroupManager().getBossGroup();
    }

    @Override
    public void start() {

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(workGroup);

        bootstrap.channel(getContext().isUseEpoll() ? EpollDatagramChannel.class : NioDatagramChannel.class);
        bootstrap.handler(new InnerSocketServerInitializer(context));
//        bootstrap.childHandler(initializer);
        bootstrap.option(ChannelOption.SO_BROADCAST, true);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);


        try {
            serverChannel = bootstrap.bind(port).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Logs.DEFAULT_LOGGER.info("bind port : " + port);
    }

    public Channel getChannel() {
        return serverChannel;
    }

    @Override
    public void stop() {
        serverChannel.close();
    }

}
