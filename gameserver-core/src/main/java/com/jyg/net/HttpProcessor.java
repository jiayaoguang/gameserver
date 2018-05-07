package com.jyg.net;

import com.jyg.bean.LogicEvent;
import com.jyg.util.FTLLoader;

import io.netty.buffer.ByteBuf;

/**
 * created by jiayaoguang at 2017年12月16日 
 * http事件处理器
 */
public abstract class HttpProcessor implements Processor<Request> {

	public final void process(LogicEvent<Request> event) {

		Request request = event.getData();
		Response response = this.createResponse(event);
		try {
			this.service(request, response);
//			fullHttpResponse = response.createDefaultFullHttpResponse();
		}catch(Exception e){
			e.printStackTrace();
			response.write500Error();
		}finally {
			
		}

		// .addListener(ChannelFutureListener.CLOSE);//关闭连接由客户端关闭或者timer
	}

	Response createResponse(LogicEvent<Request> event) {

		Response response = new Response();
		response.setChannel(event.getChannel());
		return response;
	}

	public HttpProcessor getDispatcher(String path) {
		return EventDispatcher.getInstance().getHttpProcessor(path);
	}
	
	public FTLLoader getFTLLoader() {
		return ftlLoader;
	}
	
	public abstract void service(Request request, Response response);

}
