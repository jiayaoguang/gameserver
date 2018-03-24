package com.jyg.handle;

import java.util.List;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.GeneratedMessageV3.Builder;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.jyg.net.EventDispatcher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * created by jiayaoguang at 2018年3月13日
 * protobuf编码器
 */
public class MyProtobufEncoder extends MessageToMessageEncoder<MessageLiteOrBuilder> {

	EventDispatcher dis = EventDispatcher.getInstance();

	@Override
	protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out)
			throws Exception {
		String protoName;
		
		byte[] bytes = null;
		if (msg instanceof MessageLite) {
			bytes = ((MessageLite) msg).toByteArray();
			protoName = ((MessageLite)msg).getClass().getName();
		} else if (msg instanceof MessageLite.Builder) {
			MessageLite messageLite = ((MessageLite.Builder) msg).build();
			bytes = messageLite.toByteArray();
			protoName = messageLite.getClass().getName();
		}else {
			throw new Exception("unknow message type");
		}

		if (bytes == null) {
			throw new Exception("not MessageLiteOrBuilder");
		}
		Integer eventid = dis.getEventIdByProtoName(protoName);
		
		if(eventid == null) {
			throw new Exception("unknow eventid");
		}
		
		int protoLen = 4 + bytes.length;
		ByteBuf buf = ctx.alloc().directBuffer(protoLen);
		buf.writeInt(protoLen);
		buf.writeInt(eventid);
		buf.writeBytes(bytes);
		out.add(buf);
	}
}
