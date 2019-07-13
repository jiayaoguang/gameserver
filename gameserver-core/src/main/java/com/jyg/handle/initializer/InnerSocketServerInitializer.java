package com.jyg.handle.initializer;

import com.jyg.handle.InnerSocketHandler;
import com.jyg.handle.MyProtobufDecoder;
import com.jyg.handle.MyProtobufEncoder;

/**
 * created by jiayaoguang at 2017年12月6日
 */
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class InnerSocketServerInitializer extends
		ChannelInitializer<Channel> {

	@Override
	public void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
//		pipeline.addLast(new ProtobufVarint32FrameDecoder());
		
		
		pipeline.addLast(new MyProtobufDecoder());
		
		pipeline.addLast(new MyProtobufEncoder());
		
		pipeline.addLast(new InnerSocketHandler());
		
//		pipeline.addLast(new LastHandler());
		

	}
}