package org.jyg.gameserver.core.processor;

import io.netty.channel.Channel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jyg.gameserver.core.event.DisableAccessHttpEvent;
import org.jyg.gameserver.core.event.ForbidAccessHttpEvent;
import org.jyg.gameserver.core.event.HttpRequestEvent;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.IpUtil;
import org.jyg.gameserver.core.util.Logs;

/**
 * created by jiayaoguang at 2017年12月16日 
 * http事件处理器
 */
public abstract class HttpProcessor extends AbstractProcessor<Request> {

	protected final String path;



	public HttpProcessor(String path) {
		this.path = path;
	}

	@Override
	public final void process(Session session, MsgEvent<Request> event) {
		HttpRequestEvent httpRequestEvent = (HttpRequestEvent)event;
		process(event.getMsgData() ,httpRequestEvent.getChannel());
	}

	public final void process(Request request ,Channel channel) {

//		Request request = event.getData();
		Response response = this.createResponse(channel);

		if(request.isMakeException()){
			response.write500Error();
			return;
		}

		if(!checkAccessHttp(request , response)){
			getGameConsumer().getEventManager().publishEvent(new ForbidAccessHttpEvent(request,response));
			Logs.DEFAULT_LOGGER.info("channel {} forbid access http path {} ", response.getAddr(), getPath());
			return;
		}



		if(!this.isEnableAccess()){
			Logs.DEFAULT_LOGGER.debug("channel {} forbid access , http path {} disable", response.getAddr(), getPath());
			getGameConsumer().getEventManager().publishEvent(new DisableAccessHttpEvent(request,response));
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
			String reomoteAddr = IpUtil.getChannelRemoteAddr(channel);
			Logs.DEFAULT_LOGGER.info(" exec {} cost {} mills ,ip {}", path, (System.nanoTime() - beforeExecNanoTime) / (1000L * 1000L), reomoteAddr);
		}

		// .addListener(ChannelFutureListener.CLOSE);//关闭连接由客户端关闭或者timer
	}

	private Response createResponse(Channel channel) {

		Response response = new Response(channel);
		return response;
	}

	public HttpProcessor getHttpProcessor(String path) {
		return getGameConsumer().getHttpProcessor(path);
	}



	public abstract void service(Request request, Response response);

	public String getPath() {
		return path;
	}

}
