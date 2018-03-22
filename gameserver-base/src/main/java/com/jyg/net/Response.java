package com.jyg.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * created by jiayaoguang at 2018年3月20日
 */
public class Response {

	private final ByteBuf content = Unpooled.buffer(100, 2000);
	

	public void write(String msg) {
		content.writeBytes(msg.getBytes(CharsetUtil.UTF_8));
	}
	
	public void write(byte[] bytes) {
		content.writeBytes(bytes);
	}

//	public void setContent(ByteBuf content) {
//
//		this.content = content;
//	}
	
	public ByteBuf getContent() {

		return content;
	}
//	ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
	public DefaultFullHttpResponse createDefaultFullHttpResponse() {
		return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,this.getContent());
	}

}
