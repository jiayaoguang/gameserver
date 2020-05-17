package org.jyg.gameserver.core.handle;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import org.jyg.gameserver.core.net.EventDispatcher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * created by jiayaoguang at 2018年3月13日
 * protobuf编码器
 */
@Sharable
public class MyProtobufEncoder extends MessageToByteEncoder<MessageLiteOrBuilder> {

	private final EventDispatcher eventDispatcher;

	public MyProtobufEncoder(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}


	@Override
	protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, ByteBuf buf) {
		Class<? extends MessageLite> protoClazz;
		System.out.println("111threadName : "+Thread.currentThread().getName());
		byte[] bytes = null;
		if (msg instanceof MessageLite) {
			bytes = ((MessageLite) msg).toByteArray();
			protoClazz = ((MessageLite)msg).getClass();
		} else if (msg instanceof MessageLite.Builder) {
			MessageLite messageLite = ((MessageLite.Builder) msg).build();
			bytes = messageLite.toByteArray();
			protoClazz = messageLite.getClass();
		}else {
			throw new IllegalArgumentException("Unknow message type");
		}

		if (bytes == null) {
			throw new IllegalArgumentException("not MessageLiteOrBuilder");
		}
		int eventId = eventDispatcher.getEventIdByProtoClazz(protoClazz);
		if(eventId <= 0) {
			System.out.println("unknow eventid");
			return;
		}
		
		int protoLen = 4 + bytes.length;
//		ByteBuf buf = ctx.alloc().directBuffer(protoLen);
		buf.writeInt(protoLen);
		buf.writeInt(eventId);
		buf.writeBytes(bytes);
//		out.add(buf);
	}
}
