package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.util.FTLLoader;

/**
 * created by jiayaoguang at 2017年12月16日 
 * http事件处理器
 */
public abstract class HttpProcessor extends AbstractProcessor<Request> {

	private String path;

	public final void process(LogicEvent<Request> event) {

		Request request = event.getData();
		Response response = this.createResponse(event);

		if(request.isMakeExecption()){
			response.write500Error();
			return;
		}

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

	private Response createResponse(LogicEvent<Request> event) {

		Response response = new Response();
		response.setChannel(event.getChannel());
		return response;
	}

	public HttpProcessor getHttpProcessor(String path) {
		return getEventDispatcher().getHttpProcessor(path);
	}

	public FTLLoader getFTLLoader() {
		return ftlLoader;
	}
	
	public abstract void service(Request request, Response response);

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
