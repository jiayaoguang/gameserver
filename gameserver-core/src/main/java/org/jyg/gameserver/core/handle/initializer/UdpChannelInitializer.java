package org.jyg.gameserver.core.handle.initializer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.jyg.gameserver.core.handle.UdpMsgDecoder;
import org.jyg.gameserver.core.handle.UdpMsgEncoder;
import org.jyg.gameserver.core.util.GameContext;

/**
 * create by jiayaoguang on 2024/9/7
 */
public class UdpChannelInitializer extends
		MyChannelInitializer<Channel> {

	public UdpChannelInitializer(GameContext gameContext) {
		super(gameContext);
	}

	@Override
	public void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
//		pipeline.addLast(new ProtobufVarint32FrameDecoder());


//		if(gameContext.getServerConfig().getReadOutTimeSec() > 0){
//			pipeline.addLast("idle",new IdleStateHandler(gameContext.getServerConfig().getReadOutTimeSec(),0,0 , TimeUnit.SECONDS));
//		}
//
//		pipeline.addLast("connect",new NettyConnectManageHandler(gameContext));


		pipeline.addLast("UdpMsgDecoder" , new UdpMsgDecoder(gameContext));

//		pipeline.addLast("MsgDecoder" , gameContext.getNettyHandlerFactory().createMsgDecoder());

		pipeline.addLast("UdpMsgEncoder", new UdpMsgEncoder(gameContext));



//		pipeline.addLast("byteMsgEncoder" , context.getNettyHandlerFactory().getMyByteMsgObjEncoder());


//		pipeline.addLast(new MyProtobufListEncoder(context));
		
//		pipeline.addLast(new InnerSocketHandler(context));


//		pipeline.addLast(new LastHandler());
		

	}
}