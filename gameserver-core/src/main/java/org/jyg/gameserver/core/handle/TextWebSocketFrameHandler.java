package org.jyg.gameserver.core.handle;

import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.consumer.Consumer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.Logs;

import java.util.HashMap;
import java.util.Map;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class TextWebSocketFrameHandler extends
		SimpleChannelInboundHandler<WebSocketFrame> {

//	public static Set<Channel> channels = new HashSet<>();
	private final Context context;

	public TextWebSocketFrameHandler(Context context) {
		this.context = context;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
		Channel incoming = ctx.channel();
		Logs.DEFAULT_LOGGER.info("Client:" + incoming.remoteAddress() + "在线");

		context.getConsumerManager().publicEventToDefault(EventType.SOCKET_CONNECT_ACTIVE, null, ctx.channel() , 0 );
		
		super.channelActive(ctx);
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx,
			WebSocketFrame frame) throws Exception { // (1)
		
		//|| frame instanceof BinaryWebSocketFrame
		if(frame instanceof TextWebSocketFrame ) {
			
			Channel incoming = ctx.channel();
//			msg.retain();
//			String request = msg.text();
//			TextWebSocketFrame responseFrame = new TextWebSocketFrame(frame.content());
			
			String text = ((TextWebSocketFrame) frame).text();


			context.getConsumerManager().publicEventToDefault(EventType.TEXT_MESSAGE_COME, text, ctx.channel() , 0);
			
		}else if(frame instanceof BinaryWebSocketFrame) {
			Logs.DEFAULT_LOGGER.info("this frame is BinaryWebSocketFrame");
			
		}else {
			Logs.DEFAULT_LOGGER.info("this frame is unkonwn");
		}
		
//		if(frame instanceof PingWebSocketFrame) {
//			incoming.write(new PongWebSocketFrame(frame.content().retain()));
//			return;
//		}
//		
//		if(frame instanceof CloseWebSocketFrame) {
//			
//			return;
//		}
		
		return;
		
	}
	
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
		Channel incoming = ctx.channel();
		Logs.DEFAULT_LOGGER.info("Client:" + incoming.remoteAddress() + "掉线");

		context.getConsumerManager().publicEventToDefault(EventType.SOCKET_CONNECT_INACTIVE, null, ctx.channel() , 0);
		
		super.channelInactive(ctx);
	}
	
	//------------------------start-----------------------------
	public static Map<String,String> toMap(String text){
		Map<String,String> map = new HashMap<>();
		for(String keyValue:text.split("&")) {
			int index = keyValue.indexOf('=');
			if(index<0) {
				continue;
			}
			map.put(keyValue.substring(0,index), keyValue.substring(index+1,keyValue.length()));
		}
		return map;
	}
	
	
	//-------------------------end---------------------------------------

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception { // (2)
		super.handlerAdded(ctx);
//		Channel incoming = ctx.channel();
//		for (Channel channel : channels) {
//			channel.writeAndFlush(new TextWebSocketFrame("[SERVER] - "
//					+ incoming.remoteAddress() + " 加入"));
//		}
//		channels.add(ctx.channel());
//		System.out.println("Client:" + incoming.remoteAddress() + "加入");
		Logs.DEFAULT_LOGGER.info("add");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception { // (3)
		super.handlerRemoved(ctx);
//		Channel incoming = ctx.channel();
//		for (Channel channel : channels) {
//			channel.writeAndFlush(new TextWebSocketFrame("[SERVER] - "
//					+ incoming.remoteAddress() + " 离开"));
//		}
//		System.out.println("Client:" + incoming.remoteAddress() + "离开");
//		channels.remove(ctx.channel());
		Logs.DEFAULT_LOGGER.info("remove");
	}


}