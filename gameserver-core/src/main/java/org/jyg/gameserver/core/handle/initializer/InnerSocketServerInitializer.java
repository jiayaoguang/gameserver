package org.jyg.gameserver.core.handle.initializer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.jyg.gameserver.core.handle.InnerSocketHandler;
import org.jyg.gameserver.core.handle.MyProtobufDecoder;
import org.jyg.gameserver.core.handle.MyProtobufEncoder;
import org.jyg.gameserver.core.handle.MyProtobufListEncoder;
import org.jyg.gameserver.core.util.Context;

/**
 * created by jiayaoguang at 2017年12月6日
 */

public class InnerSocketServerInitializer extends
		MyChannelInitializer<Channel> {

	public InnerSocketServerInitializer(Context context) {
		super(context);
	}

	@Override
	public void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
//		pipeline.addLast(new ProtobufVarint32FrameDecoder());
		
		
		pipeline.addLast(new MyProtobufDecoder(context));
		
		pipeline.addLast(new MyProtobufEncoder(context));
		
		pipeline.addLast(new InnerSocketHandler(context.getGlobalQueue()));
		
//		pipeline.addLast(new LastHandler());
		

	}
}