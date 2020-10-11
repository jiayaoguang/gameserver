package org.jyg.gameserver.core.handle;

import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.consumer.Consumer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

import java.io.IOException;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpRequest> {

	private final Consumer defaultConsumer;

	public HttpServerHandler(Consumer defaultConsumer) {
		this.defaultConsumer = defaultConsumer;
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

		System.out.println(
				((FullHttpRequest) msg).content().refCnt() + " ," + Thread.currentThread().getName() + ">>>>>>>>>>..");

		Request request = this.createRequest((HttpRequest) msg);

		defaultConsumer.publicEvent(EventType.HTTP_MESSAGE_COME, request, ctx.channel());

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
		cause.printStackTrace();
		ctx.close();
	}

	// AtomicLong requestid = new AtomicLong(1);

	public Request createRequest(HttpRequest httpRequest) throws IOException {
		Request request = new Request(httpRequest);
		// request.setRequestid(requestid.getAndIncrement());
		return request;
	}

}
