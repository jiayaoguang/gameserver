package com.jyg.net;

import com.google.protobuf.MessageLite;
import com.jyg.bean.LogicEvent;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public abstract class  HttpProcessor implements Processor{
	
	public void process(LogicEvent event)throws Exception{
		
		this.process(null,null);
	}
	
	public abstract void process(HttpRequest request,HttpResponse response);
	
}

