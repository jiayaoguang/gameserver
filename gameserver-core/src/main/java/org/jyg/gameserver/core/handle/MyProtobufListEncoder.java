package org.jyg.gameserver.core.handle;

import cn.hutool.core.util.ZipUtil;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.Logs;

import java.util.List;

/**
 * created by jiayaoguang at 2018年3月13日
 * protobuf list 编码器
 * 需要考虑数组产生的字节码过大的情况
 */
@Sharable
public class MyProtobufListEncoder extends MessageToByteEncoder<List<? extends MessageLite>> {

	protected final Context context;

	public MyProtobufListEncoder(Context context) {
		this.context = context;
	}


	@Override
	protected void encode(ChannelHandlerContext ctx, List<? extends MessageLite> msgList, ByteBuf buf) {


		for (MessageLite msg:msgList){
			Class<? extends MessageLite> protoClazz = msg.getClass();
			Logs.DEFAULT_LOGGER.info("deal threadName : "+Thread.currentThread().getName());
			byte[] bytes = msg.toByteArray();
			if (bytes == null) {
				throw new IllegalArgumentException("not MessageLiteOrBuilder");
			}
			int eventId = context.getMsgIdByProtoClass(protoClazz);
			if(eventId <= 0) {
				Logs.DEFAULT_LOGGER.info("unknow eventid");
				continue;
			}

//			if(context.getServerConfig().isUseGzip()){
//				bytes = ZipUtil.gzip(bytes);
//			}


			int protoLen = 4 + bytes.length;
//		ByteBuf buf = ctx.alloc().directBuffer(protoLen);
			buf.writeInt(protoLen);
			buf.writeInt(eventId);
			buf.writeBytes(bytes);
		}


//		out.add(buf);
	}





}
