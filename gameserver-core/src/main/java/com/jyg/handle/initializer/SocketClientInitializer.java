package com.jyg.handle.initializer;

import com.jyg.handle.LastCodec;
import com.jyg.handle.MyProtobufDecoder;
import com.jyg.handle.MyProtobufEncoder;

/**
 * created by jiayaoguang at 2017年12月6日
 */
import com.jyg.util.Context;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketClientInitializer extends
		MyChannelInitializer<Channel> {

	DefaultEventExecutorGroup defaultEventExecutorGroup = new DefaultEventExecutorGroup(
			3,
            new ThreadFactory() {

		private AtomicInteger threadIndex = new AtomicInteger(0);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "NettyServerCodecThread_" + this.threadIndex.incrementAndGet());
		}
	});

	public SocketClientInitializer(Context context) {
		super(context);
	}

	@Override
	public void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
//		pipeline.addLast(new ProtobufVarint32FrameDecoder());
		
		pipeline.addLast(new MyProtobufDecoder(context));
		
		pipeline.addLast(new MyProtobufEncoder(context.getEventDispatcher()));
		
		pipeline.addLast(new LastCodec());
		

	}
}