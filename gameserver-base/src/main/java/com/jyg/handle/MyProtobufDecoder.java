package com.jyg.handle;

import java.io.IOException;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.jyg.net.EventDispatcher;
import com.jyg.net.ProtoProcessor;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * created by jiayaoguang at 2018年3月13日
 */
public class MyProtobufDecoder extends LengthFieldBasedFrameDecoder {

	public MyProtobufDecoder() {
		super(1024 * 64, 0, 4, 0, 4);
	}

	EventDispatcher dis = EventDispatcher.getInstance();

	@Override
	public ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
		ByteBuf buf = buffer.slice(index, length);
		int eventId = buf.readInt();
		Parser<MessageLite> parser = ((ProtoProcessor) dis.getRpcProcessor(eventId)).getProtoParser();

		try (ByteBufInputStream bis = new ByteBufInputStream(buf)) {
			MessageLite m = parser.parseFrom(bis);

			// TODO public
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Unpooled.EMPTY_BUFFER;
	}

}
