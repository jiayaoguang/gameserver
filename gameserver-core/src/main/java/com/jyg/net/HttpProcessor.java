package com.jyg.net;

import com.jyg.bean.LogicEvent;

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
			event.getChannel().writeAndFlush( response.create500FullHttpResponse() );
		}finally {
			//TODO
			ByteBuf buf = response.getContent();
			if(buf.readableBytes()==0) {
				buf.release(response.getContent().refCnt());
				System.out.println("buf.refCnt()" + buf.refCnt());
			}
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
	
	public abstract void service(Request request, Response response);

}
