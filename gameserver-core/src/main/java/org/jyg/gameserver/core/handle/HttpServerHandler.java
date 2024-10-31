package org.jyg.gameserver.core.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import org.jyg.gameserver.core.event.HttpRequestEvent;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

import java.io.IOException;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpRequest> {

	private final GameContext gameContext;

	public HttpServerHandler(GameContext gameContext) {
		this.gameContext = gameContext;
	}

	// 是否是线程同步的http
	// private final boolean isSynHttp;
	//
	// public HttpServerHandler() {
	// isSynHttp = true;
	// }
	//
	// public HttpServerHandler(boolean isSynHttp) {
	// this.isSynHttp = isSynHttp;
	// }

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		 super.channelActive(ctx);
		// httpChannels.put(id.getAndIncrement(), ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelInactive(ctx);

	}


	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {

		Logs.DEFAULT_LOGGER.debug(
				((FullHttpRequest) msg).content().refCnt() + " ," + Thread.currentThread().getName() + ">>>>>>>>>>..");

		Request request = this.createRequest((HttpRequest) msg);

		HttpRequestEvent httpRequestEvent = new HttpRequestEvent(request , ctx.channel() );

		gameContext.getConsumerManager().publishEvent(gameContext.getMainConsumerId() , httpRequestEvent);

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

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		Logs.DEFAULT_LOGGER.error("make exception : " ,cause);
		ctx.close();
	}

	// AtomicLong requestid = new AtomicLong(1);

	public Request createRequest(HttpRequest httpRequest) throws IOException {
		Request request = new Request(httpRequest);
		// request.setRequestid(requestid.getAndIncrement());
		return request;
	}

}
