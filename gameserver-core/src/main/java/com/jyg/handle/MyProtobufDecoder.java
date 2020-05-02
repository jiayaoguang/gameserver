package com.jyg.handle;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.jyg.enums.EventType;
import com.jyg.net.EventDispatcher;
import com.jyg.processor.ProtoProcessor;
import com.jyg.util.IGlobalQueue;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;

/**
 * created by jiayaoguang at 2018年3月13日 protobuf解码器
 */
public class MyProtobufDecoder extends LengthFieldBasedFrameDecoder {

	private final IGlobalQueue globalQueue;

	public MyProtobufDecoder(IGlobalQueue globalQueue) {
		super(1024 * 64, 0, 4, 0, 4);
		this.globalQueue = globalQueue;
	}

	private EventDispatcher dis = EventDispatcher.getInstance();

	// @Override
	// public ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int
	// index, int length) {
	// ByteBuf buf = buffer.slice(index, length);
	// int eventId = buf.readInt();
	// ProtoProcessor<? extends GeneratedMessageV3> protProcessor =
	// dis.getSocketProcessor(eventId);
	//
	// if(protProcessor==null) {
	// return Unpooled.EMPTY_BUFFER;
	// }
	//
	// Parser<? extends MessageLite> parser = protProcessor.getProtoParser();
	//
	// try (ByteBufInputStream bis = new ByteBufInputStream(buf)) {
	// MessageLite messageLite = parser.parseFrom(bis);
	//
	// GlobalQueue.publicEvent(EventType.RPC_MSG_COME, messageLite, ctx.channel() ,
	// eventId );
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return Unpooled.EMPTY_BUFFER;
	// }

	
	@Override
	public Object decode(ChannelHandlerContext ctx, ByteBuf in) {
		ByteBuf frame = null;
		//TODO crc
		try {
			frame = (ByteBuf) super.decode(ctx, in);
			
			int eventId = frame.readInt();
			System.out.println("cnf:"+frame.refCnt());
			ProtoProcessor<? extends GeneratedMessageV3> protProcessor = dis.getSocketProcessor(eventId);
			if (protProcessor == null) {
				return null;
			}
			Parser<? extends MessageLite> parser = protProcessor.getProtoParser();

			try (ByteBufInputStream bis = new ByteBufInputStream(frame)) {
				MessageLite messageLite = parser.parseFrom(bis);
				globalQueue.publicEvent(EventType.RPC_MSG_COME, messageLite, ctx.channel(), eventId);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			ctx.channel().close().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					// log
				}
			});
		}
		// finally {
		// if(null!=frame)
		// frame.release();
		// }

		return null;
	}
	
	@Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }

	
}
