package org.jyg.gameserver.core.processor;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.FTLLoader;
import org.jyg.gameserver.core.util.MyLoggerFactory;

/**
 * created by jiayaoguang at 2017年12月16日 
 * http事件处理器
 */
public abstract class HttpProcessor extends AbstractProcessor<Request> {

	private String path;


	public HttpProcessor() {

	}

	public HttpProcessor(String path) {
		this.path = path;
	}

	@Override
	public final void process(Session session, LogicEvent<Request> event) {
		process(event);
	}

	public final void process(LogicEvent<Request> event) {

		Request request = event.getData();
		Response response = this.createResponse(event);

		if(request.isMakeExecption()){
			response.write500Error();
			return;
		}
		long beforeExecTime = System.currentTimeMillis();
		try {
			this.service(request, response);
//			fullHttpResponse = response.createDefaultFullHttpResponse();
		}catch(Exception e){
			logger.error(" make exception {} " , ExceptionUtils.getStackTrace(e));
			response.write500Error();
		}finally {

			long afterExecTime = System.currentTimeMillis();
			MyLoggerFactory.DEFAULT_LOGGER.info(" exec {} cost {} mills ,ip {}" , path , (afterExecTime - beforeExecTime) , response.getChannel().remoteAddress().toString());
		}

		// .addListener(ChannelFutureListener.CLOSE);//关闭连接由客户端关闭或者timer
	}

	private Response createResponse(LogicEvent<Request> event) {

		Response response = new Response();
		response.setChannel(event.getChannel());
		return response;
	}

	public HttpProcessor getHttpProcessor(String path) {
		return getDefaultConsumer().getHttpProcessor(path);
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
