package org.jyg.gameserver.core.processor;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.FTLLoader;
import org.jyg.gameserver.core.util.Logs;

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
	public final void process(Session session, EventData<Request> event) {
		process(event);
	}

	public final void process(EventData<Request> event) {

		Request request = event.getData();
		Response response = this.createResponse(event);

		if(request.isMakeExecption()){
			response.write500Error();
			return;
		}
		long beforeExecNanoTime = System.nanoTime();
		try {
			this.service(request, response);
//			fullHttpResponse = response.createDefaultFullHttpResponse();
		}catch(Exception e){
			String exceptionMsg = ExceptionUtils.getStackTrace(e);
			logger.error(" make exception {} " , exceptionMsg);
			response.write500Error(exceptionMsg);
		}finally {

			Logs.DEFAULT_LOGGER.info(" exec {} cost {} mills ,ip {}" , path , (System.nanoTime() - beforeExecNanoTime)/(1000L*1000L) , response.getChannel().remoteAddress().toString());
		}

		// .addListener(ChannelFutureListener.CLOSE);//关闭连接由客户端关闭或者timer
	}

	private Response createResponse(EventData<Request> event) {

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
