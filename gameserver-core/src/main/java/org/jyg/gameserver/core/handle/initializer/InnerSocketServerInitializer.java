package org.jyg.gameserver.core.handle.initializer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import org.jyg.gameserver.core.handle.*;
import org.jyg.gameserver.core.util.Context;

import java.util.concurrent.TimeUnit;

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


		if(context.getServerConfig().getReadOutTimeSec() > 0){
			pipeline.addLast("idle",new IdleStateHandler(context.getServerConfig().getReadOutTimeSec(),0,0 , TimeUnit.SECONDS));
		}

		pipeline.addLast("connect",new NettyConnectManageHandler(context));

		pipeline.addLast("MsgDecoder" , context.getNettyHandlerFactory().createMsgDecoder());

		if(context.getServerConfig().isNeedMergeProto()){
			pipeline.addLast("MsgMergeEncoder" , context.getNettyHandlerFactory().createMsgMergeHandler(context));
		}else {
			pipeline.addLast("MsgEncoder", context.getNettyHandlerFactory().getMsgEncoder());
		}

//		pipeline.addLast("byteMsgEncoder" , context.getNettyHandlerFactory().getMyByteMsgObjEncoder());


//		pipeline.addLast(new MyProtobufListEncoder(context));
		
//		pipeline.addLast(new InnerSocketHandler(context));


//		pipeline.addLast(new LastHandler());
		

	}
}