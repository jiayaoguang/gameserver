package org.jyg.gameserver.core.startup;

import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.MpscQueueGameConsumer;
import org.jyg.gameserver.core.handle.initializer.MyChannelInitializer;
import org.jyg.gameserver.core.handle.initializer.SocketClientInitializer;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;

/**
 * create by jiayaoguang at 2018年3月5日
 * 远程端口连接
 */

public class TcpClient extends AbstractBootstrap{

//	public String host = "127.0.0.1"; // ip地址
//	public int port = 6789; // 端口
	
	// 通过nio方式来接收连接和处理连接
	private final Bootstrap bootstrap = new Bootstrap();
	private Channel channel;
	private Session session;

	private  String host;
	private  int port;

	@Deprecated
	public TcpClient()  {
		this(new GameContext(new MpscQueueGameConsumer()) , "" , 0);

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

	public TcpClient( int port) {
		this(new GameContext(new MpscQueueGameConsumer()), "127.0.0.1", port);
	}


	public TcpClient(String host, int port) {
		this(new GameContext(new MpscQueueGameConsumer()), host, port);
	}

	public TcpClient(GameContext gameContext, String host, int port) {
		super(gameContext);
		this.host = host;
		this.port = port;
	}


	public TcpClient(GameConsumer gameConsumer, String host, int port) {
		super(gameConsumer);
		this.host = host;
		this.port = port;
	}


	@Override
	public void doStart(){
		doStart(new SocketClientInitializer(getGameContext()));
	}


	private void doStart(MyChannelInitializer<Channel> channelInitializer){

		bootstrap.group(getGameContext().getEventLoopGroupManager().getWorkGroup());
		bootstrap.channel( getGameContext().isUseEpoll() ? EpollSocketChannel.class : NioSocketChannel.class);
		bootstrap.handler(channelInitializer);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_RCVBUF, 64*1024);
		bootstrap.option(ChannelOption.SO_SNDBUF, 64*1024);
//		bootstrap.option(ChannelOption.SO_LINGER, 0);
		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);


		if(port > 0){
			connect();
		}

		Logs.DEFAULT_LOGGER.info("client start success");
	}

	public Session connect(){
		if (channel != null) {
			channel.close();
			Logs.DEFAULT_LOGGER.info(" close old channel ");
		}


		ChannelFuture channelFuture = null;
		try {
			channelFuture = bootstrap.connect(host, port).sync();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		if(channelFuture == null || !channelFuture.isSuccess()){
			logger.error(" connect fail ");
			return null;
		}
		logger.error(" connect ip {} port {} success " , host ,port);
		start = true;

		channel = channelFuture.channel();


		session = getDefaultConsumer().getChannelManager().doTcpClientLink(channel);

		return session;
	}

	// 连接服务端
	@Deprecated
	public Session connect(String host,int port) {
		this.host = host;
		this.port = port;

		return connect();
	}


	public void write(MessageLite msg) {
//		checkConnect();
		channel.writeAndFlush(msg);
//		System.out.println("客户端发送数据>>>>");
	}

	public void write( ByteMsgObj byteMsgObj) {
//		checkConnect();
		channel.writeAndFlush( byteMsgObj);
//		System.out.println("客户端发送数据>>>>");
	}

	public void write( MessageLite.Builder msgBuilder) throws IOException {
//		checkConnect();
		write(msgBuilder.build());
//		System.out.println("客户端发送数据>>>>");
	}

	public void write(Object data) {
//		checkConnect();
		channel.writeAndFlush(data);
//		System.out.println("客户端发送数据>>>>");
	}
	
//	private Channel getChannel() {
//		return channel;
//	}

	public void close() {

		if(session !=null) {
			session.stop();
		}

	}

	public void checkConnect(){
		if(channel == null || !channel.isActive()){
			logger.error("connect lose, try reconnect");
			connect();
		}

	}
	public boolean isConnectAvailable(){
		if(channel == null || !channel.isActive()){
			return false;
		}
		return true;
	}

	@Deprecated
	public Session getSession() {
		return session;
	}


	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
