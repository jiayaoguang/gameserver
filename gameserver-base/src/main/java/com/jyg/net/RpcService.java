package com.jyg.net;

import com.jyg.handle.MyProtobufDecoder;
import com.jyg.handle.MyProtobufEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class RpcService extends Service {

	
	public RpcService(int port) throws Exception {
		super(port , new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new MyProtobufDecoder());
				pipeline.addLast(new MyProtobufEncoder());
				
			}
			
		});
	}

	
}
