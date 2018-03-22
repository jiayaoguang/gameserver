package com.jyg.net;

import java.io.IOException;
import java.util.Map;

import com.jyg.bean.LogicEvent;
import com.jyg.util.RequestParser;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;

/**
 * created by jiayaoguang at 2017年12月16日
 * http事件处理器
 */
public abstract class  HttpProcessor implements Processor<Request>{
	
	public final void process(LogicEvent<Request> event)throws Exception{
		
		Request request = event.getData();
		Response response = this.createResponse(event);
		
		this.service(request,response);
		
		DefaultFullHttpResponse fullHttpResponse = response.createDefaultFullHttpResponse();
		
		fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
		fullHttpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
		fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.getContent().readableBytes());
		
		event.getChannel().writeAndFlush( fullHttpResponse );
		//.addListener(ChannelFutureListener.CLOSE);
	}
	
	 
	 public Response createResponse(LogicEvent<Request> event) {
		 
		 Response response = new Response();
		 
		 return response;
	 }
	
	public abstract void service(Request request,Response response);
	
}

