package org.jyg.gameserver.core.handle;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.Logs;

/**
 * created by jiayaoguang at 2018年3月13日
 * protobuf编码器
 */
@Sharable
public class MsgEncoder extends MessageToByteEncoder<Object> {

	protected final Context context;

	public MsgEncoder(Context context) {
		this.context = context;
	}


	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf buf) {
		if( msg instanceof MessageLite){
			MessageLite messageLite = (MessageLite)msg;
			AllUtil.writeToBuf(context , messageLite , buf);
		}else if( msg instanceof ByteMsgObj){
			ByteMsgObj byteMsgObj = (ByteMsgObj)msg;
			AllUtil.writeToBuf(context , byteMsgObj , buf);
		}else {
			Logs.DEFAULT_LOGGER.error("unknown msg type : {}" , msg.getClass().getCanonicalName());
		}
//		out.add(buf);
	}
}
