package org.jyg.gameserver.core.net;

import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.handle.initializer.MyChannelInitializer;
import org.jyg.gameserver.core.manager.EventLoopGroupManager;
import org.jyg.gameserver.core.util.RemotingUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * create by jiayaoguang at 2018年3月6日
 */

public abstract class TcpService extends AbstractService {

	private final MyChannelInitializer<Channel> initializer;

	private final int port;

	private final boolean isHttp;

	public TcpService(int port, MyChannelInitializer<Channel> initializer) {
		this(port, initializer, false);
	}

	public TcpService(int port, MyChannelInitializer<Channel> initializer , boolean isHttp) {
		super(initializer.getDefaultConsumer());
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
	 *
	 * @return
	 * @throws Exception
	 */
	public synchronized void start() throws InterruptedException {

		if (initializer == null) {
			throw new IllegalArgumentException("initializer must not null");
		}

		ServerBootstrap bootstrap = new ServerBootstrap();

		EventLoopGroupManager eventLoopGroupManager = initializer.getContext().getEventLoopGroupManager();
		bootstrap.group(eventLoopGroupManager.getBossGroup(), eventLoopGroupManager.getWorkGroup());


		bootstrap.channel(getDefaultConsumer().getContext().isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
//		bootstrap.handler(new LoggingHandler(LogLevel.INFO));
		bootstrap.childHandler(initializer);

		bootstrap.option(ChannelOption.SO_REUSEADDR, true);
		// tcp等待三次握手队列的长度
		bootstrap.option(ChannelOption.SO_BACKLOG, 1024);

		if(getDefaultConsumer().getContext().isUseEpoll()){
			bootstrap.option(EpollChannelOption.TCP_CORK, false);
		}

		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		// 指定等待时间为0，此时调用主动关闭时不会发送FIN来结束连接，而是直接将连接设置为CLOSE状态，
		// 清除套接字中的发送和接收缓冲区，直接对对端发送RST包。
		if(!isHttp){
			bootstrap.childOption(ChannelOption.SO_LINGER, 0);
		}
		bootstrap.childOption(ChannelOption.SO_RCVBUF, 64 * 1024);
		bootstrap.childOption(ChannelOption.SO_SNDBUF, 64 * 1024);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);// maybe useless
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		String host = getContext().getServerConfig().getHost();
		if(StringUtils.isEmpty(host)){
			bootstrap.bind(port).sync().channel();
		}else {
			bootstrap.bind(host,port).sync().channel();
		}

		System.out.println("正在开启端口监听，端口号 :" + port);
	}

	public void stop() {

	}


}
