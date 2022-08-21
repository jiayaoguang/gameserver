package org.jyg.gameserver.core.handle;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.GameContext;

/**
 * created by jiayaoguang at 2018年3月13日
 * protobuf编码器
 */
@Sharable
@Deprecated
public class MyByteMsgObjEncoder extends MessageToByteEncoder<ByteMsgObj> {

	protected final GameContext gameContext;

	public MyByteMsgObjEncoder(GameContext gameContext) {
		this.gameContext = gameContext;
	}


	@Override
	protected void encode(ChannelHandlerContext ctx, ByteMsgObj msg, ByteBuf buf) {
		AllUtil.writeToBuf(gameContext, msg , buf);
//		out.add(buf);
	}
}
