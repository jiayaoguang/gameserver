package org.jyg.gameserver.core.handle;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.IpUtil;
import org.jyg.gameserver.core.util.Logs;

/**
 * created by jiayaoguang at 2018年3月13日
 * protobuf编码器
 */
@Sharable
public class MsgEncoder extends MessageToByteEncoder<Object> {

	protected final GameContext gameContext;

	private final boolean printResponseMsg;

	public MsgEncoder(GameContext gameContext) {
		this.gameContext = gameContext;
		this.printResponseMsg = gameContext.getServerConfig().isPrintResponseMsg();
	}


	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf buf) {
		if( msg instanceof MessageLite){
			MessageLite messageLite = (MessageLite)msg;
			AllUtil.writeToBuf(gameContext, messageLite , buf);
		}else if( msg instanceof ByteMsgObj){
			ByteMsgObj byteMsgObj = (ByteMsgObj)msg;
			AllUtil.writeToBuf(gameContext, byteMsgObj , buf);
		}else {
			Logs.DEFAULT_LOGGER.error("encode msg fail , unknown msg type : {}" , msg.getClass().getCanonicalName());
		}
//		out.add(buf);

		if(printResponseMsg){
			Logs.DEFAULT_LOGGER.info("response session {} msg {}", IpUtil.getChannelRemoteIp(ctx.channel()), msg.getClass().getSimpleName());
		}

	}
}
