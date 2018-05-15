package com.jyg.handle;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.jyg.bean.LogicEvent;
import com.jyg.consumers.EventConsumerFactory;
import com.jyg.enums.EventType;
import com.jyg.net.EventDispatcher;
import com.jyg.net.Request;
import com.jyg.timer.DelayCloseTimer;
import com.jyg.util.RequestParser;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
/**
 * created by jiayaoguang at 2018年4月12日
 * 异步http
 */
@Deprecated
public class AsyncHttpServerHandler extends SimpleChannelInboundHandler<HttpRequest> {



	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}
	
	
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
//		super.channelActive(ctx);
//		httpChannels.put(id.getAndIncrement(), ctx.channel());
		//5秒后关闭
		EventDispatcher.getInstance().addTimer(new DelayCloseTimer(ctx.channel(),5));
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelInactive(ctx);
		
		
	}
	

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {

			System.out.println(((FullHttpRequest) msg).content().refCnt() + " ," + Thread.currentThread().getName()
					+ ">>>>>>>>>>..");
			
			Request request = this.createRequest((HttpRequest) msg);
			
			LogicEvent<Request> event = new LogicEvent<>();
			event.setData(request);
			event.setChannel(ctx.channel());
			event.setChannelEventType(EventType.HTTP_MSG_COME);
			EventConsumerFactory.newEventConsumer().onEvent(event);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	
	
	AtomicLong requestid = new AtomicLong(1);
	
	public Request createRequest(HttpRequest httpRequest) throws IOException {
		Map<String, String> params = RequestParser.parse(httpRequest);
		Request request = new Request(httpRequest);
		request.setParametersMap(params);
		request.setRequestid(requestid.getAndIncrement());
		return request;
	}


}
