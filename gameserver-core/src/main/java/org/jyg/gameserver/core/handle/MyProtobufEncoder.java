package org.jyg.gameserver.core.handle;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Context;

/**
 * created by jiayaoguang at 2018年3月13日
 * protobuf编码器
 */
@Sharable
@Deprecated
public class MyProtobufEncoder extends MessageToByteEncoder<MessageLite> {

	protected final Context context;

	public MyProtobufEncoder(Context context) {
		this.context = context;
	}


	@Override
	protected void encode(ChannelHandlerContext ctx, MessageLite msg, ByteBuf buf) {
		AllUtil.writeToBuf(context , msg , buf);
//		out.add(buf);
	}
}
