package com.jyg.handle.initializer;

import com.jyg.handle.LastCodec;
import com.jyg.handle.MyProtobufDecoder;
import com.jyg.handle.MyProtobufEncoder;

/**
 * created by jiayaoguang at 2017年12月6日
 */
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class SocketClientInitializer extends
		ChannelInitializer<SocketChannel> { 

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
//		pipeline.addLast(new ProtobufVarint32FrameDecoder());
		
		pipeline.addLast(new MyProtobufDecoder());
		
		pipeline.addLast(new MyProtobufEncoder());
		
		pipeline.addLast(new LastCodec());
		

	}
}