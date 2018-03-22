package com.jyg.net;

import java.util.concurrent.ThreadFactory;

import com.jyg.handle.WebSocketServerInitializer;
import com.jyg.util.Constants;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class WebSocketService extends Service {

	public WebSocketService(int port) throws Exception {
		super(port , new WebSocketServerInitializer());
	}
}
