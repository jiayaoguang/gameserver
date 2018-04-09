package com.jyg.net;

import com.jyg.bean.LogicEvent;

import io.netty.handler.codec.http.DefaultFullHttpResponse;

/**
 * created by jiayaoguang at 2017年12月16日 
 * http事件处理器
 */
public abstract class HttpProcessor implements Processor<Request> {

	public void process(LogicEvent<Request> event) {

		Request request = event.getData();
		Response response = this.createResponse();
		DefaultFullHttpResponse fullHttpResponse = null;
		try {
			this.service(request, response);
			fullHttpResponse = response.createDefaultFullHttpResponse();
		}catch(Exception e){
			e.printStackTrace();
			fullHttpResponse = response.create500FullHttpResponse();
		}

		event.getChannel().writeAndFlush(fullHttpResponse);
		// .addListener(ChannelFutureListener.CLOSE);//关闭连接由客户端关闭或者timer
	}

	Response createResponse() {

		Response response = new Response();

		return response;
	}

	public HttpProcessor getDispatcher(String path) {
		return EventDispatcher.getInstance().getHttpProcessor(path);
	}
	
	public abstract void service(Request request, Response response);

}
