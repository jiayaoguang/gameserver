package org.jyg.gameserver.core.handle.initializer;

import io.netty.handler.timeout.IdleStateHandler;
import org.jyg.gameserver.core.handle.*;

/**
 * created by jiayaoguang at 2017年12月6日
 */
import org.jyg.gameserver.core.util.GameContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

import java.util.concurrent.TimeUnit;

public class SocketClientInitializer extends
		MyChannelInitializer<Channel> {


	public SocketClientInitializer(GameContext gameContext) {
		super(gameContext);
	}

	@Override
	public void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
//		pipeline.addLast(new ProtobufVarint32FrameDecoder());


		if(gameContext.getServerConfig().getClientWriteOutTimeSec() > 0 || gameContext.getServerConfig().getReadOutTimeSec() > 0){
			pipeline.addLast("idle", new IdleStateHandler(gameContext.getServerConfig().getReadOutTimeSec()
					, gameContext.getServerConfig().getClientWriteOutTimeSec(), 0, TimeUnit.SECONDS));
		}

		pipeline.addLast("connect",new NettyClientConnectManageHandler(gameContext));


//		if(context.getServerConfig().isNeedMergeProto()){
//			pipeline.addLast(context.getNettyHandlerFactory().getProtoMergeHandler());
//		}else {
//			pipeline.addLast("protoMsgEncoder" , context.getNettyHandlerFactory().getMyProtobufEncoder());
//		}

//		pipeline.addLast("byteMsgEncoder" , context.getNettyHandlerFactory().getMyByteMsgObjEncoder());
		pipeline.addLast("MsgDecoder" , gameContext.getNettyHandlerFactory().createMsgDecoder());
		pipeline.addLast("MsgEncoder" , gameContext.getNettyHandlerFactory().getMsgEncoder());


//		pipeline.addLast(new MyProtobufListEncoder(context));
		
//		pipeline.addLast(new LastCodec());
		

	}
}