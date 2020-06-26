package org.jyg.gameserver.core.handle;

import cn.hutool.core.util.ZipUtil;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jyg.gameserver.core.util.Context;

/**
 * created by jiayaoguang at 2018年3月13日
 * protobuf编码器
 */
@Sharable
public class MyProtobufEncoder extends MessageToByteEncoder<MessageLite> {

	protected final Context context;

	public MyProtobufEncoder(Context context) {
		this.context = context;
	}


	@Override
	protected void encode(ChannelHandlerContext ctx, MessageLite msg, ByteBuf buf) {
		Class<? extends MessageLite> protoClazz = msg.getClass();
		System.out.println("deal threadName : "+Thread.currentThread().getName());
		byte[] bytes = msg.toByteArray();
//		if (msg instanceof GeneratedMessageV3) {
//			bytes = msg.toByteArray();
//			protoClazz = ((GeneratedMessageV3)msg).getClass();
//		} else if (msg instanceof GeneratedMessageV3.Builder) {
////			GeneratedMessageV3 messageLite =  ((GeneratedMessageV3.Builder) msg).build();
////			bytes = messageLite.toByteArray();
////			protoClazz = messageLite.getClass();
//			throw new IllegalArgumentException("Unknow message type");
//		}else {
//			throw new IllegalArgumentException("Unknow message type");
//		}

		if (bytes == null) {
			throw new IllegalArgumentException("not MessageLiteOrBuilder");
		}
		int eventId = context.getMsgIdByProtoClass(protoClazz);
		if(eventId <= 0) {
			System.out.println("unknow eventid");
			return;
		}

//		if(context.getServerConfig().isUseGzip()){
//			bytes = ZipUtil.gzip(bytes);
//		}
		
		int protoLen = 4 + bytes.length;
//		ByteBuf buf = ctx.alloc().directBuffer(protoLen);
		buf.writeInt(protoLen);
		buf.writeInt(eventId);
		buf.writeBytes(bytes);
//		out.add(buf);
	}
}
