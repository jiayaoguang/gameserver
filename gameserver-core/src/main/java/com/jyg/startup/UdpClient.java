package com.jyg.startup;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.MessageLiteOrBuilder;
import com.jyg.handle.initializer.SocketClientInitializer;
import com.jyg.net.EventDispatcher;
import com.jyg.processor.ProtoProcessor;
import com.jyg.session.Session;
import com.jyg.util.RemotingUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
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

public class UdpClient extends AbstractBootstrap{

//	public String host = "127.0.0.1"; // ip地址
//	public int port = 6789; // 端口

	// 通过nio方式来接收连接和处理连接
	private static EventLoopGroup group = new NioEventLoopGroup();
	private Bootstrap bootstrap = new Bootstrap();
	private Channel channel;
	private Session session;
	public UdpClient()  {

	}

//	public UdpClient(ChannelInitializer<Channel> channelInitializer) {
//		System.out.println("客户端成功启动...");
//		bootstrap.group(group);
//		bootstrap.channel( RemotingUtil.useEpoll() ? EpollDatagramChannel.class : NioDatagramChannel.class);
//		bootstrap.handler(channelInitializer);
//        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
//		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
//	}


	public void doStart(){
		bootstrap.group(group);
		bootstrap.channel( RemotingUtil.useEpoll() ? EpollDatagramChannel.class : NioDatagramChannel.class);
		bootstrap.handler(new SocketClientInitializer(getContext()));
		bootstrap.option(ChannelOption.SO_REUSEADDR, true);
		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
	}


	public Channel bind(int port) throws InterruptedException {

		channel = bootstrap.bind( port).sync().channel();

		return channel;
	}
	

	public Channel getChannel() {
		return channel;
	}
	
	public void close() {
		if(channel !=null && channel.isOpen()) {
			channel.close();
		}
		group.shutdownGracefully();
	}

}
