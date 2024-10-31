package org.jyg.gameserver.core.handle;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.jyg.gameserver.core.util.Logs;

/**
 * created by jiayaoguang at 2018年3月24日 测试用，添加在管道尾部的编解码器
 */
public class LastCodec extends MessageToMessageCodec<Object, Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		Logs.DEFAULT_LOGGER.info("msg [{}] write" , msg.getClass().getName());
		out.add(msg);

	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		Logs.DEFAULT_LOGGER.info("msg [{}] read" , msg.getClass().getName());
		out.add(msg);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Logs.DEFAULT_LOGGER.info("inactive");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		Logs.DEFAULT_LOGGER.error("make exception : " ,cause);
		ctx.close();
	}
}
