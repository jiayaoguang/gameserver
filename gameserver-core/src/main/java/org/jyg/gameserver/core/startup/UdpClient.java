package org.jyg.gameserver.core.startup;

import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.data.UdpMsgInfo;
import org.jyg.gameserver.core.handle.initializer.UdpChannelInitializer;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.session.Session;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.IOException;

/**
 * create by jiayaoguang at 2018年3月5日
 * 远程端口连接
 */

public class UdpClient extends AbstractBootstrap {

//	public String host = "127.0.0.1"; // ip地址
//	public int port = 6789; // 端口

    // 通过nio方式来接收连接和处理连接
    private static EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private Session session;

    private final String serverHost;
    private final int serverPort;

    public UdpClient(String serverHost , int serverPort) {
        this.serverPort = serverPort;
        this.serverHost = serverHost;
    }

//	public UdpClient(ChannelInitializer<Channel> channelInitializer) {
//		System.out.println("客户端成功启动...");
//		bootstrap.group(group);
//		bootstrap.channel( RemotingUtil.useEpoll() ? EpollDatagramChannel.class : NioDatagramChannel.class);
//		bootstrap.handler(channelInitializer);
//        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
//		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
//	}


    public void doStart() {
        bootstrap.group(group);
        bootstrap.channel(getGameContext().isUseEpoll() ? EpollDatagramChannel.class : NioDatagramChannel.class);
        bootstrap.handler(new UdpChannelInitializer(getGameContext()));
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        //绑定到任意本地地址，因为我们不关心从哪里发送数据报
        try {
            channel = bootstrap.bind(0).sync().channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public Channel getChannel() {
        return channel;
    }

    public void close() {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        group.shutdownGracefully();
    }


    public void write(MessageLite msg) {
//		checkConnect();
        writeObjMsg(msg);
//		System.out.println("客户端发送数据>>>>");
    }

    public void write(ByteMsgObj byteMsgObj) {
//		checkConnect();
        writeObjMsg(byteMsgObj);
//		System.out.println("客户端发送数据>>>>");
    }

    public void write(MessageLite.Builder msgBuilder) throws IOException {
//		checkConnect();
        writeObjMsg(msgBuilder.build());
//		System.out.println("客户端发送数据>>>>");
    }

    private void writeObjMsg(Object msg) {
//		checkConnect();

        UdpMsgInfo udpMsgInfo = new UdpMsgInfo(serverHost,serverPort,msg);
        channel.writeAndFlush(udpMsgInfo);
//		System.out.println("客户端发送数据>>>>");
    }
}
