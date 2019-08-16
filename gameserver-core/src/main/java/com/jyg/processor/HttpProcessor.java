package com.jyg.processor;

import com.jyg.bean.LogicEvent;
import com.jyg.net.EventDispatcher;
import com.jyg.net.Request;
import com.jyg.net.Response;
import com.jyg.util.FTLLoader;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by jiayaoguang at 2017年12月16日 
 * http事件处理器
 */
public abstract class HttpProcessor extends AbstractProcessor<Request> {

	public final void process(LogicEvent<Request> event) {

		Request request = event.getData();
		Response response = this.createResponse(event);
		try {
			this.service(request, response);
//			fullHttpResponse = response.createDefaultFullHttpResponse();
		}catch(Exception e){
			logger.error(" make exception {} " , e);
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
