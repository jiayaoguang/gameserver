package com.jyg.startup;

import java.io.IOException;

import com.google.protobuf.MessageLiteOrBuilder;
import com.jyg.bean.EventIdAndMessageLiteOrBuilder;
import com.jyg.handle.RpcServerInitializer;

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

public class Client {

//	public String host = "127.0.0.1"; // ip地址
//	public int port = 6789; // 端口
	
	/// 通过nio方式来接收连接和处理连接
	private EventLoopGroup group = new NioEventLoopGroup();
	private Bootstrap bootstrap = new Bootstrap();
	private Channel channel;

	public Client(String host,int port) throws InterruptedException, IOException {
		this(host,port,new RpcServerInitializer());
	}

	public Client(String host,int port,ChannelInitializer<SocketChannel> channelInitializer) throws InterruptedException, IOException {
		System.out.println("客户端成功启动...");
		bootstrap.group(group);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(channelInitializer);
		
		bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_RCVBUF, 8*1024);
		bootstrap.option(ChannelOption.SO_SNDBUF, 8*1024);
		bootstrap.option(ChannelOption.SO_LINGER, 0);
		
		// 连接服务端
		channel = bootstrap.connect(host, port).sync().channel();
	}
	

	public void write(int eventId, MessageLiteOrBuilder msg) throws IOException {
		channel.writeAndFlush(new EventIdAndMessageLiteOrBuilder(eventId, msg));
		System.out.println("客户端发送数据>>>>");
	}

}
