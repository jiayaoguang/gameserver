package com.jyg.startup;

import java.io.IOException;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.MessageLiteOrBuilder;
import com.jyg.handle.initializer.SocketClientInitializer;
import com.jyg.net.EventDispatcher;
import com.jyg.net.ProtoProcessor;
import com.jyg.session.Session;
import com.jyg.timer.IdleTimer;
import com.jyg.util.RemotingUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * create by jiayaoguang at 2018年3月5日
 * 远程端口连接
 */

public class InnerClient extends AbstractBootstrap {

//	public String host = "127.0.0.1"; // ip地址
//	public int port = 6789; // 端口
	
	// 通过nio方式来接收连接和处理连接
	private static EventLoopGroup group = new NioEventLoopGroup();
	private Bootstrap bootstrap = new Bootstrap();
	private Channel channel;
	private Session session;
	public InnerClient()  {
		this(new SocketClientInitializer());
//		try {
//			this.registerSendEventIdByProto(ProtoEnum.P_COMMON_REQUEST_PING.getEventId(), p_common.p_common_request_ping.class);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		//注册pong处理器
//		try {
//			this.registerSocketEvent(ProtoEnum.P_COMMON_RESPONSE_PONG.getEventId() , new PongProtoProcessor());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
	}

	public InnerClient(ChannelInitializer<SocketChannel> channelInitializer) {
		System.out.println("客户端成功启动...");
		bootstrap.group(group);
		bootstrap.channel( RemotingUtil.useEpoll() ? EpollSocketChannel.class : NioSocketChannel.class);
		bootstrap.handler(channelInitializer);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_RCVBUF, 8*1024);
		bootstrap.option(ChannelOption.SO_SNDBUF, 8*1024);
		bootstrap.option(ChannelOption.SO_LINGER, 0);
		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
	}
	
	// 连接服务端
	public Channel connect(String host,int port) throws InterruptedException {
		channel = bootstrap.connect(host, port).sync().channel();
		
		EventDispatcher.getInstance().addTimer( new IdleTimer(channel) );
		
		return channel;
	}
	
	
	
//	public void registerSocketEvent(int eventid, ProtoProcessor<? extends GeneratedMessageV3> protoprocessor) throws Exception {
//		EventDispatcher.getInstance().registerSocketEvent(eventid, protoprocessor);
//	}
//	
//	public void registerSendEventIdByProto(int eventId,Class<? extends GeneratedMessageV3> protoClazz) throws Exception {
//		EventDispatcher.getInstance().registerSendEventIdByProto( eventId, protoClazz);
//	}

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
