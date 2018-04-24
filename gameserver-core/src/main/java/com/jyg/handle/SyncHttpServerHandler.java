package com.jyg.handle;

import java.io.IOException;
import java.util.Map;

import com.jyg.enums.EventType;
import com.jyg.net.Request;
import com.jyg.util.GlobalQueue;
import com.jyg.util.RequestParser;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;

public class SyncHttpServerHandler extends SimpleChannelInboundHandler {

	
	// 是否是线程同步的http
//	private final boolean isSynHttp;
//
//	public HttpServerHandler() {
//		isSynHttp = true;
//	}
//
//	public HttpServerHandler(boolean isSynHttp) {
//		this.isSynHttp = isSynHttp;
//	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}
	
	
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelActive(ctx);
//		httpChannels.put(id.getAndIncrement(), ctx.channel());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelInactive(ctx);
		
		
	}
	

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if (msg instanceof HttpRequest) {

			System.out.println(((FullHttpRequest) msg).content().refCnt() + " ," + Thread.currentThread().getName()
					+ ">>>>>>>>>>..");
			
			Request request = this.createRequest((HttpRequest) msg);
			
			GlobalQueue.publicEvent( EventType.HTTP_MSG_COME , request, ctx.channel() );
			
//			} else {
//				LogicEvent<Request> event = new LogicEvent<>();
//				event.setData(request);
//				event.setChannel(ctx.channel());
//				event.setChannelEventType(EventType.HTTP_MSG_COME);
//				EventConsumerFactory.newEventConsumer().onEvent(event);
//			}

			// HttpRequest request = (HttpRequest) msg;

			// boolean keepAlive = HttpUtil.isKeepAlive(request);

			// HttpEvent event = new HttpEvent();
			// event.setUri(request.uri());
			// Map<String,String> params = RequestParser.parse((FullHttpRequest)request);
			// event.setData(params);
			// ByteBuf bytebuf = processor.process(event);

			// FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
			// Unpooled.wrappedBuffer(CONTENT));
			// response.headers().set(CONTENT_TYPE, "text/plain");
			// response.headers().setInt(CONTENT_LENGTH,
			// response.content().readableBytes());
			//
			//
			// if (!keepAlive) {
			// ctx.write(response).addListener(ChannelFutureListener.CLOSE);
			// } else {
			// response.headers().set(CONNECTION, KEEP_ALIVE);
			// ctx.write(response);
			// }
		}
		ReferenceCountUtil.release(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	
	
//	AtomicLong requestid = new AtomicLong(1);
	
	public Request createRequest(HttpRequest httpRequest) throws IOException {
		Map<String, String> params = RequestParser.parse(httpRequest);
		Request request = new Request(httpRequest);
		request.setParametersMap(params);
//		request.setRequestid(requestid.getAndIncrement());
		return request;
	}
	
}
