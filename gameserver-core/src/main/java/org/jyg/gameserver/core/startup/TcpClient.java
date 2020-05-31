package org.jyg.gameserver.core.startup;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import org.jyg.gameserver.core.handle.initializer.MyChannelInitializer;
import org.jyg.gameserver.core.handle.initializer.SocketClientInitializer;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.RemotingUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
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


	public TcpClient(Context context)  {
		super(context);

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

	@Override
	public void doStart(){
		start(new SocketClientInitializer(getContext()));
	}


	public void start(MyChannelInitializer<Channel> channelInitializer){
		System.out.println("客户端成功启动...");
		bootstrap.group(getContext().getEventLoopGroupManager().getWorkGroup());
		bootstrap.channel( RemotingUtil.useEpoll() ? EpollSocketChannel.class : NioSocketChannel.class);
		bootstrap.handler(channelInitializer);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_RCVBUF, 8*1024);
		bootstrap.option(ChannelOption.SO_SNDBUF, 8*1024);
		bootstrap.option(ChannelOption.SO_LINGER, 0);
		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		globalQueue.start();
	}

	public TcpClient(MyChannelInitializer<Channel> channelInitializer) {

	}
	
	// 连接服务端
	public Channel connect(String host,int port) throws InterruptedException {

		ChannelFuture channelFuture = bootstrap.connect(host, port).sync();

		if(!channelFuture.isSuccess()){
			logger.error(" connect fail ");
			return null;
		}

		isStart = true;

		channel = channelFuture.channel();
		
//		EventDispatcher.getInstance().addTimer( new IdleTimer(channel) );

		return channel;
	}
	

	public void write( MessageLite msg) throws IOException {
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
		getContext().getEventLoopGroupManager().stopAllEventLoop();
	}

}
