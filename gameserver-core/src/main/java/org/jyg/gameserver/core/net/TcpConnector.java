package org.jyg.gameserver.core.net;

import io.netty.channel.*;
import io.netty.channel.epoll.EpollChannelOption;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.handle.initializer.MyChannelInitializer;
import org.jyg.gameserver.core.manager.EventLoopGroupManager;
import org.jyg.gameserver.core.util.Logs;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * create by jiayaoguang at 2018年3月6日
 */

public abstract class TcpConnector extends AbstractConnector {

	private final MyChannelInitializer<Channel> initializer;

	private final int port;

	private final boolean isHttp;

	private ServerBootstrap serverBootstrap;

	private Channel serverChannel;

	public TcpConnector(int port, MyChannelInitializer<Channel> initializer) {
		this(port, initializer, false);
	}

	public TcpConnector(int port, MyChannelInitializer<Channel> initializer , boolean isHttp) {
		super(initializer.getGameContext());
		if (port < 0) {
			throw new IllegalArgumentException("port number cannot be negative ");
		}
		this.port = port;
		this.initializer = initializer;
		this.isHttp = isHttp;
	}

	public ChannelInitializer<Channel> getInitializer() {
		return initializer;
	}

	/**
	 * 启动端口监听方法
	 */
	public synchronized void start() {

		if (initializer == null) {
			throw new IllegalArgumentException("initializer must not null");
		}

		serverBootstrap = new ServerBootstrap();

		EventLoopGroupManager eventLoopGroupManager = initializer.getGameContext().getEventLoopGroupManager();
		serverBootstrap.group(eventLoopGroupManager.getBossGroup(), eventLoopGroupManager.getWorkGroup());


		serverBootstrap.channel(getGameContext().isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
//		bootstrap.handler(new LoggingHandler(LogLevel.INFO));
		serverBootstrap.childHandler(initializer);

		serverBootstrap.option(ChannelOption.SO_REUSEADDR, true);
		// tcp等待三次握手队列的长度
		serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);

//		if(getGameContext().isUseEpoll()){
//			serverBootstrap.option(EpollChannelOption.TCP_CORK, false);
//		}

		serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		// 指定等待时间为0，此时调用主动关闭时不会发送FIN来结束连接，而是直接将连接设置为CLOSE状态，
		// 清除套接字中的发送和接收缓冲区，直接对对端发送RST包。
		if(!isHttp){
			serverBootstrap.childOption(ChannelOption.SO_LINGER, 0);
		}

		if(gameContext.getServerConfig().getSocketRcvBuff() < 1024){
			throw new IllegalArgumentException("ServerConfig().getSocketRcvBuff() < 1024");
		}

		if(gameContext.getServerConfig().getSocketSndBuff() < 1024){
			throw new IllegalArgumentException("ServerConfig().getSocketSndBuff() < 1024");
		}

		serverBootstrap.childOption(ChannelOption.SO_RCVBUF, gameContext.getServerConfig().getSocketRcvBuff());
		serverBootstrap.childOption(ChannelOption.SO_SNDBUF, gameContext.getServerConfig().getSocketSndBuff());
		serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);// maybe useless
		serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		WriteBufferWaterMark writeBufferWaterMark = new WriteBufferWaterMark(128 * 1024, 256 * 1024);
		serverBootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, writeBufferWaterMark);


		String host = getGameContext().getServerConfig().getHost();

		ChannelFuture bindChannelFuture;
		if(StringUtils.isEmpty(host)){
			bindChannelFuture = serverBootstrap.bind(port);
		}else {
			bindChannelFuture = serverBootstrap.bind(host,port);
		}

		try {
			serverChannel = bindChannelFuture.sync().channel();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		Logs.DEFAULT_LOGGER.info("start server port success,port : {}" , port);
	}

	public void stop() {

		if(serverChannel != null){
			try {
				serverChannel.close().sync();
			} catch (InterruptedException ignore) {
				// Ignore
			}
		}
	}


}
