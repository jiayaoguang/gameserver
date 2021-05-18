package org.jyg.gameserver.core.handle;

import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.consumer.Consumer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jyg.gameserver.core.util.Logs;

/**
 * created by jiayaoguang at 2018年4月9日
 */
public class InnerSocketHandler extends ChannelInboundHandlerAdapter {

	private final Consumer defaultConsumer;

	public InnerSocketHandler(Consumer defaultConsumer) {
		this.defaultConsumer = defaultConsumer;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
		Channel incoming = ctx.channel();
		Logs.DEFAULT_LOGGER.info("Client:" + incoming.remoteAddress() + "在线");
		
		defaultConsumer.publicEvent(EventType.SOCKET_CONNECT_ACTIVE, null, ctx.channel() , 0);
		
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
		Channel incoming = ctx.channel();
		Logs.DEFAULT_LOGGER.info("Client:" + incoming.remoteAddress() + "掉线");
		
		defaultConsumer.publicEvent(EventType.SOCKET_CONNECT_INACTIVE, null, ctx.channel() , 0);
		
		super.channelInactive(ctx);
	}

	
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		Channel incoming = ctx.channel();
		Logs.DEFAULT_LOGGER.info("Client:" + incoming.remoteAddress() + "异常");
		// 当出现异常就关闭连接
		cause.printStackTrace();
		ctx.close();
	}
	
}

