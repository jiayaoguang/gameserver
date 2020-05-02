package com.jyg.handle.initializer;

import com.jyg.handle.InnerSocketHandler;
import com.jyg.handle.MyProtobufDecoder;
import com.jyg.handle.MyProtobufEncoder;

/**
 * created by jiayaoguang at 2017年12月6日
 */
import com.jyg.util.IGlobalQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

public class InnerSocketServerInitializer extends
		MyChannelInitializer<Channel> {

	public InnerSocketServerInitializer(IGlobalQueue globalQueue) {
		super(globalQueue);
	}

	@Override
	public void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
//		pipeline.addLast(new ProtobufVarint32FrameDecoder());
		
		
		pipeline.addLast(new MyProtobufDecoder(globalQueue));
		
		pipeline.addLast(new MyProtobufEncoder());
		
		pipeline.addLast(new InnerSocketHandler(globalQueue));
		
//		pipeline.addLast(new LastHandler());
		

	}
}