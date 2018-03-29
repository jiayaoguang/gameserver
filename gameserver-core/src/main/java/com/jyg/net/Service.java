package com.jyg.net;

import java.util.concurrent.ThreadFactory;

import com.jyg.handle.WebSocketServerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * create by jiayaoguang at 2018年3月6日
 */

public abstract class Service {
	protected static final EventLoopGroup bossGroup = new NioEventLoopGroup(1,
			(Runnable r) -> new Thread(r, "ACCEPT_THREAD"));

	protected static final EventLoopGroup workGroup = new NioEventLoopGroup(8, new ThreadFactory() {

		private int threadIndex = 1;

		@Override
		public Thread newThread(Runnable r) {

			return new Thread(r, "IO_THREAD_" + threadIndex++);
		}
		
	});

	
	private ChannelInitializer<SocketChannel> initializer = new WebSocketServerInitializer();

	private final int port;


	public Service(int port, ChannelInitializer<SocketChannel> initializer) throws Exception {
		if (port < 0) {
			throw new Exception("port number cannot be negative ");
		}
		this.port = port;
		this.initializer = initializer;
	}

	public ChannelInitializer<SocketChannel> getInitializer() {
		return initializer;
	}

	public void setInitializer(ChannelInitializer<SocketChannel> initializer) {
		this.initializer = initializer;
	}

	/**
	 * 启动端口监听方法
	 * 
	 * @return
	 * @throws Exception
	 */
	public final void start() throws Exception {

		if (initializer == null) {
			throw new Exception("initializer must is not null");
		}

		ServerBootstrap b = new ServerBootstrap();

		b.group(bossGroup, workGroup);

		b.channel(NioServerSocketChannel.class);

		b.handler(new LoggingHandler(LogLevel.INFO));

		b.childHandler(initializer);

		b.option(ChannelOption.SO_REUSEADDR, true);

		b.option(ChannelOption.SO_BACKLOG, 400);

		b.option(ChannelOption.SO_KEEPALIVE, false);

		b.option(ChannelOption.TCP_NODELAY, true);

		b.option(ChannelOption.SO_RCVBUF, 64 * 1024);

		b.option(ChannelOption.SO_SNDBUF, 64 * 1024);

		b.childOption(ChannelOption.SO_LINGER, 1000);

		b.childOption(ChannelOption.SO_KEEPALIVE, false);

		b.childOption(ChannelOption.TCP_NODELAY, true);
		b.bind(port).sync().channel();
		System.out.println("正在开启端口监听，端口号 :" + port);
	}

	/**
	 * 停止服务
	 */
	public static void shutdown() {
		bossGroup.shutdownGracefully();
		workGroup.shutdownGracefully();
	}
}
