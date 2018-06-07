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

public abstract class AbstractBootstrap {

	
	public AbstractBootstrap()  {
		
	}
	
	public void registerSocketEvent(int eventid, ProtoProcessor<? extends GeneratedMessageV3> protoprocessor) throws Exception {
		EventDispatcher.getInstance().registerSocketEvent(eventid, protoprocessor);
	}
	
	public void registerSendEventIdByProto(int eventId,Class<? extends GeneratedMessageV3> protoClazz) throws Exception {
		EventDispatcher.getInstance().registerSendEventIdByProto( eventId, protoClazz);
	}


}
