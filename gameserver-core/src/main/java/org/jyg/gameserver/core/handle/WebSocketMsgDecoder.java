package org.jyg.gameserver.core.handle;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

import java.util.HashMap;
import java.util.Map;

/**
 * created by jiayaoguang at 2022年2月4日
 */
public class WebSocketMsgDecoder extends
		SimpleChannelInboundHandler<WebSocketFrame> {

//	public static Set<Channel> channels = new HashSet<>();
	private final GameContext gameContext;

	public WebSocketMsgDecoder(GameContext gameContext) {
		this.gameContext = gameContext;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
		Channel incoming = ctx.channel();
		Logs.DEFAULT_LOGGER.info("Client:" + incoming.remoteAddress() + "在线");

		gameContext.getConsumerManager().publicEventToDefault(EventType.SOCKET_CONNECT_ACTIVE, null, ctx.channel() , 0 );
		
		super.channelActive(ctx);
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx,
			WebSocketFrame frame) throws Exception { // (1)
		
		//|| frame instanceof BinaryWebSocketFrame
		if (frame == null) {
			return;
		}


		ByteBuf msgbyteBuf ;


		if(frame instanceof TextWebSocketFrame){
			TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame)frame;
//			msgBytes = textWebSocketFrame.text().getBytes(StandardCharsets.UTF_8);
			msgbyteBuf = frame.content();
		}else if(frame instanceof BinaryWebSocketFrame){
			BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame)frame;

			msgbyteBuf = frame.content();
		}else {
			Logs.DEFAULT_LOGGER.error(" webscoket frame type not support : {} ", frame.getClass().getSimpleName());
			return;
		}



		try{

			int msgId = msgbyteBuf.readInt();
//            Logs.DEFAULT_LOGGER.debug("cnf:" + frame.refCnt());
			AbstractMsgCodec<?> msgCodec = gameContext.getMsgCodec(msgId);
			if (msgCodec == null) {
				Logs.DEFAULT_LOGGER.error(" protoParser not found ,id : {} ", msgId);
				return;
			}

			int readableBytes = msgbyteBuf.readableBytes();


			final byte[] dstBytes = new byte[msgbyteBuf.readableBytes()];;
			if(readableBytes > 0){
				msgbyteBuf.getBytes(msgbyteBuf.readerIndex(), dstBytes);
			}else {
//                LOGGER.info(" readableBytes <= 0 ,id : {} ", msgId);
			}

			Object msgObj;

			try{
				msgObj = msgCodec.decode(dstBytes);
			}catch (Exception e){
				Logs.DEFAULT_LOGGER.error(" msg decode make exception, msgCodec type : {}  , exception {}", msgCodec.getClass().getSimpleName(), e.getCause());
				throw e;
			}


			gameContext.getConsumerManager().publicEventToDefault(EventType.REMOTE_MSG_COME, msgObj, ctx.channel(), msgId);

//			switch (msgCodec.getMsgType()) {
//				case PROTO:
//					MessageLite messageLite = (MessageLite) msgObj;
//					gameContext.getConsumerManager().publicEventToDefault( EventType.REMOTE_MSG_COME, messageLite, ctx.channel(), msgId);
//					break;
//				case BYTE_OBJ:
//					ByteMsgObj byteMsgObj = (ByteMsgObj) msgObj;
//					gameContext.getConsumerManager().publicEventToDefault(EventType.REMOTE_MSG_COME, byteMsgObj, ctx.channel(), msgId);
//					break;
//				default:
//					Logs.DEFAULT_LOGGER.error(" unknown msg type type : {} ", msgCodec.getMsgType());
//					break;
//			}


		}catch (Exception e){
			e.printStackTrace();
			final String addrRemote = AllUtil.getChannelRemoteAddr(ctx.channel());
			ctx.channel().close().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					Logs.DEFAULT_LOGGER.info("closeChannel: close the connection to remote address[{}] result: {}", addrRemote,
							future.isSuccess());
				}
			});
		} finally {

//            Logs.DEFAULT_LOGGER.info("frame.refCnt() : " + frame.refCnt());

		}
		
	}
	
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
		Channel incoming = ctx.channel();
		Logs.DEFAULT_LOGGER.info("Client:" + incoming.remoteAddress() + "掉线");

		gameContext.getConsumerManager().publicEventToDefault(EventType.SOCKET_CONNECT_INACTIVE, null, ctx.channel() , 0);
		
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