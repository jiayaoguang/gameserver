package com.jyg.net;

import java.io.UnsupportedEncodingException;

import com.jyg.bean.LogicEvent;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * created by jiayaoguang at 2018年3月21日
 */
public class NotFoundHttpProcessor extends HttpProcessor {
	private HttpResponse notFoundResponse;

	private ByteBuf bytebuf;
	
	private byte[] bytes;

	public NotFoundHttpProcessor() {
		this("<html><head></head><body><div align='center'><h1>404 not found </h1></div><body></html>");
	}

	public NotFoundHttpProcessor(String msg) {

		bytes =msg.getBytes(CharsetUtil.UTF_8);
//		bytebuf = Unpooled.wrappedBuffer(bytes);
//		notFoundResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, bytebuf);
	}

//	public void process(LogicEvent<HttpRequest> event) throws Exception {
//		bytebuf = Unpooled.wrappedBuffer(bytes);
//		notFoundResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, bytebuf);
//		event.getChannel().writeAndFlush(notFoundResponse);
////		.addListener(ChannelFutureListener.CLOSE);
//		
//	}
	
	

	@Override
	public void service(Request request, Response response) {
		response.write(bytes);
	}

}
