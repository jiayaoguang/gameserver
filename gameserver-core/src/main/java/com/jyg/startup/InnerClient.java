package com.jyg.startup;

import java.io.IOException;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.MessageLiteOrBuilder;
import com.jyg.handle.SocketServerInitializer;
import com.jyg.net.EventDispatcher;
import com.jyg.net.ProtoProcessor;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * create by jiayaoguang at 2018年3月5日
 * 远程端口连接
 */

public class InnerClient {

//	public String host = "127.0.0.1"; // ip地址
//	public int port = 6789; // 端口
	
	/// 通过nio方式来接收连接和处理连接
	private static EventLoopGroup group = new NioEventLoopGroup();
	private Bootstrap bootstrap = new Bootstrap();//TODO
	private Channel channel;

	public InnerClient() {
		this(new SocketServerInitializer());
	}

	public InnerClient(ChannelInitializer<SocketChannel> channelInitializer) {
		System.out.println("客户端成功启动...");
		bootstrap.group(group);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(channelInitializer);
		
		bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_RCVBUF, 8*1024);
		bootstrap.option(ChannelOption.SO_SNDBUF, 8*1024);
		bootstrap.option(ChannelOption.SO_LINGER, 0);
	}
	
	// 连接服务端
	public Channel connect(String host,int port) throws InterruptedException {
		channel = bootstrap.connect(host, port).sync().channel();
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
	
	public void close() {
		channel.close();
		group.shutdownGracefully();
	}

}
