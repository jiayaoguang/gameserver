package org.jyg.gameserver.core.handle;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.GameContext;

/**
 * created by jiayaoguang at 2018年3月13日
 * protobuf编码器
 */
@Sharable
@Deprecated
public class MyProtobufEncoder extends MessageToByteEncoder<MessageLite> {

	protected final GameContext gameContext;

	public MyProtobufEncoder(GameContext gameContext) {
		this.gameContext = gameContext;
	}


	@Override
	protected void encode(ChannelHandlerContext ctx, MessageLite msg, ByteBuf buf) {
		AllUtil.writeToBuf(gameContext, msg , buf);
//		out.add(buf);
	}
}
