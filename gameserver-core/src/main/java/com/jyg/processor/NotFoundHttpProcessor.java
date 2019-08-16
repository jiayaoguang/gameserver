package com.jyg.processor;

import com.jyg.net.Request;
import com.jyg.net.Response;
import com.jyg.processor.HttpProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import javax.annotation.concurrent.ThreadSafe;

/**
 * created by jiayaoguang at 2018年3月21日
 */
public class NotFoundHttpProcessor extends HttpProcessor {
	private HttpResponse notFoundResponse;

	private ByteBuf bytebuf;
	
	private final byte[] bytes;

	public NotFoundHttpProcessor() {
		this("<html><head></head><body><div align='center'><h1>404 not found </h1></div><body></html>");
	}

	public NotFoundHttpProcessor(String htmlText) {

		bytes = htmlText.getBytes(CharsetUtil.UTF_8);
//		bytebuf = Unpooled.wrappedBuffer(bytes);
//		notFoundResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, bytebuf);
	}

//	public void process(LogicEvent<HttpRequest> event) {
//		bytebuf = Unpooled.wrappedBuffer(bytes);
//		notFoundResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, bytebuf);
//		event.getChannel().writeAndFlush(notFoundResponse);
////		.addListener(ChannelFutureListener.CLOSE);
//		
//	}
	
	

	@Override
	public void service(Request request, Response response) {
		response.writeAndFlush(bytes);
	}

}
