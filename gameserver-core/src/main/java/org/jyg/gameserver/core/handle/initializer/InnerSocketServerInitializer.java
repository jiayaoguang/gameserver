package org.jyg.gameserver.core.handle.initializer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.jyg.gameserver.core.handle.InnerSocketHandler;
import org.jyg.gameserver.core.handle.MyProtobufDecoder;
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
		
		
		pipeline.addLast("msgDecoder" , new MyProtobufDecoder(context));

		if(context.getServerConfig().isNeedMergeProto()){
			pipeline.addLast("protoMsgMergeEncoder" , context.getNettyHandlerFactory().createProtoMergeHandler(context));
		}else {
			pipeline.addLast("protoMsgEncoder" , context.getNettyHandlerFactory().getMyProtobufEncoder());
		}

		pipeline.addLast("byteMsgEncoder" , context.getNettyHandlerFactory().getMyByteMsgObjEncoder());


//		pipeline.addLast(new MyProtobufListEncoder(context));
		
		pipeline.addLast(new InnerSocketHandler(context));
		
//		pipeline.addLast(new LastHandler());
		

	}
}