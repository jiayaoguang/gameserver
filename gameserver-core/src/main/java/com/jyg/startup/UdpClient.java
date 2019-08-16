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

public class UdpClient {

//	public String host = "127.0.0.1"; // ip地址
//	public int port = 6789; // 端口

	// 通过nio方式来接收连接和处理连接
	private static EventLoopGroup group = new NioEventLoopGroup();
	private Bootstrap bootstrap = new Bootstrap();
	private Channel channel;
	private Session session;
	public UdpClient()  {
		this(new SocketClientInitializer());
	}

	public UdpClient(ChannelInitializer<Channel> channelInitializer) {
		System.out.println("客户端成功启动...");
		bootstrap.group(group);
		bootstrap.channel( RemotingUtil.useEpoll() ? EpollDatagramChannel.class : NioDatagramChannel.class);
		bootstrap.handler(channelInitializer);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
	}
	
	// 连接服务端
	public Channel connect(String host,int port) throws InterruptedException {
		channel = bootstrap.connect(host, port).sync().channel();
		
		return channel;
	}

	public Channel bind(int port) throws InterruptedException {
		channel = bootstrap.bind( port).sync().channel();

		return channel;
	}
	
	
	
	public void registerSocketEvent(int eventid, ProtoProcessor<? extends GeneratedMessageV3> protoprocessor) throws Exception {
		EventDispatcher.getInstance().registerSocketEvent(eventid, protoprocessor);
	}

	public void registerSendEventIdByProto(int eventId,Class<? extends GeneratedMessageV3> protoClazz) throws Exception {
		EventDispatcher.getInstance().registerSendEventIdByProto( eventId, protoClazz);
	}

	public void write( MessageLiteOrBuilder msg) throws IOException {
		channel.writeAndFlush( msg);
//		System.out.println("客户端发送数据>>>>");
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
