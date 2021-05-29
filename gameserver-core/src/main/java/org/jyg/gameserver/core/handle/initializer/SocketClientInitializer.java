package org.jyg.gameserver.core.handle.initializer;

import io.netty.handler.timeout.IdleStateHandler;
import org.jyg.gameserver.core.handle.*;

/**
 * created by jiayaoguang at 2017年12月6日
 */
import org.jyg.gameserver.core.util.Context;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

import java.util.concurrent.TimeUnit;

public class SocketClientInitializer extends
		MyChannelInitializer<Channel> {


	public SocketClientInitializer(Context context) {
		super(context);
	}

	@Override
	public void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
//		pipeline.addLast(new ProtobufVarint32FrameDecoder());





//		if(context.getServerConfig().isNeedMergeProto()){
//			pipeline.addLast(context.getNettyHandlerFactory().getProtoMergeHandler());
//		}else {
//			pipeline.addLast("protoMsgEncoder" , context.getNettyHandlerFactory().getMyProtobufEncoder());
//		}

//		pipeline.addLast("byteMsgEncoder" , context.getNettyHandlerFactory().getMyByteMsgObjEncoder());
		pipeline.addLast("MsgDecoder" , context.getNettyHandlerFactory().createMsgDecoder());
		pipeline.addLast("MsgEncoder" , context.getNettyHandlerFactory().getMsgEncoder());


		pipeline.addLast("idle",new IdleStateHandler(0,10,0 , TimeUnit.SECONDS));

		pipeline.addLast("connect",new NettyClientConnectManageHandler(context));

//		pipeline.addLast(new MyProtobufListEncoder(context));
		
		pipeline.addLast(new LastCodec());
		

	}
}