package org.jyg.gameserver.core.handle;

import cn.hutool.core.util.ZipUtil;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.util.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by jiayaoguang at 2018年3月13日 protobuf解码器
 */
public class MyProtobufDecoder extends LengthFieldBasedFrameDecoder {

	protected static final Logger LOGGER = LoggerFactory.getLogger(MyProtobufDecoder.class);

	private final Context context;

	public MyProtobufDecoder(Context context) {
		super(1024 * 64, 0, 4, 0, 4);
		this.context = context;
	}

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
	public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf frame = null;
		//TODO crc
//		try {
			frame = (ByteBuf) super.decode(ctx, in);
			if(frame == null){
				return null;
			}
			int msgId = frame.readInt();
			System.out.println("cnf:"+frame.refCnt());
			Parser<? extends MessageLite> protoParser = context.getProtoParserByMsgId(msgId);
			if (protoParser == null) {
				LOGGER.error(" protoParser not found ,id : {} ",msgId);
				return null;
			}
		try (ByteBufInputStream bis = new ByteBufInputStream(frame)) {
			final MessageLite messageLite;
			if (context.getServerConfig().isUseGzip()) {
				messageLite = protoParser.parseFrom(ZipUtil.unGzip(bis , bis.available()));
			}else {
				messageLite = protoParser.parseFrom(bis);
			}
			context.getGlobalQueue().publicEvent(EventType.RPC_MSG_COME, messageLite, ctx.channel(), msgId);
		}

//		}
//		catch (Exception e) {
//			ctx.channel().close().addListener(new ChannelFutureListener() {
//				@Override
//				public void operationComplete(ChannelFuture future) throws Exception {
//					// log
//				}
//			});
//			e.printStackTrace();
//		}
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
