package org.jyg.gameserver.core.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jyg.gameserver.core.event.ChannelConnectEvent;
import org.jyg.gameserver.core.event.ChannelDisconnectEvent;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

/**
 * created by jiayaoguang at 2018年4月9日
 */
@Deprecated
public class InnerSocketHandler extends ChannelInboundHandlerAdapter {

	private final GameContext gameContext;

	public InnerSocketHandler(GameContext gameContext) {
		this.gameContext = gameContext;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
		Channel incoming = ctx.channel();
		Logs.DEFAULT_LOGGER.info("Client:" + incoming.remoteAddress() + "在线");

		gameContext.getConsumerManager().publishcEvent(gameContext.getMainConsumerId(), new ChannelConnectEvent(ctx.channel()));
		
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
		Channel incoming = ctx.channel();
		Logs.DEFAULT_LOGGER.info("Client:" + incoming.remoteAddress() + "掉线");

		gameContext.getConsumerManager().publishcEvent(gameContext.getMainConsumerId(), new ChannelDisconnectEvent(ctx.channel()));
		
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

