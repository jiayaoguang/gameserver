package com.jyg.net;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;

/**
 * created by jiayaoguang at 2018年3月20日
 */
public class Response {
	
	
	//要发给客户端的内容
	//TODO
	private final ByteBuf content = Unpooled.directBuffer(2000, 20000);
	
	StringBuilder sb = new StringBuilder(2000);
	
	private String contentType =  "text/html; charset=UTF-8";
	
	public static final String CONTENT_TYPE_JSON = "application/json";
	
	public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
	
	//要发给客户端的cookis
	private List<Cookie> cookies = new LinkedList<>();
	
	// 500错误提示信息
	private static final byte[] internalServerErrorBytes = 
			"<html><head></head><body><div align='center'><h1>500 Internal Server Error</h1></div><body></html>"
			.getBytes();

	private Channel channel;
	
	
	public Response() {
		
	}
	
	public Response(Channel channel) {
		this.channel = channel;
	}
	
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public void flush() {
		this.getChannel().writeAndFlush(this.createDefaultFullHttpResponse());
	}
	
	public void writeAndFlush(String msg) {
		this.write(msg);
		this.getChannel().writeAndFlush(this.createDefaultFullHttpResponse());
	}
	
	public void writeAndFlush(byte[] bs) {
		this.write(bs);
		this.getChannel().writeAndFlush(this.createDefaultFullHttpResponse());
	}
	
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public void write(String msg) {
		content.writeBytes(msg.getBytes(CharsetUtil.UTF_8));
	}

	public void write(byte[] bytes) {
		content.writeBytes(bytes);
	}

	// public void setContent(ByteBuf content) {
	//
	// this.content = content;
	// }

	ByteBuf getContent() {

		return content;
	}
	/**
	 * 添加一个cookie
	 */
	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}
	
	// ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
	/**
	 * 创建一个返回给客户端的回复
	 * @return 给客户端的回复
	 */
	public DefaultFullHttpResponse createDefaultFullHttpResponse() {
		DefaultFullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
				HttpResponseStatus.OK, this.getContent());
		HttpHeaders headers = fullHttpResponse.headers();
		headers.set(HttpHeaderNames.CONTENT_TYPE, this.contentType);
		headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
		headers.set(HttpHeaderNames.CONTENT_LENGTH, getContent().readableBytes());
		headers.set(HttpHeaderNames.CONTENT_ENCODING, CharsetUtil.UTF_8);
		// cookie
//		Cookie cookie = new DefaultCookie("jygsessionid", genericSessionId());
//		cookie.setMaxAge(60*60);
//		//cookie.setDomain(System.getenv("USERDOMAIN"));
//		cookie.setPath("/");
//		cookies.add(cookie);
		if(cookies.size()>0) {
			headers.set(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookies) );
		}
		
		return fullHttpResponse;
	}
	
	/**
	 * 创建指定返回码的回复
	 * @param responseStatus http状态码
	 * @param bytes 回复内容
	 * @return 指定返回码的回复
	 */
	private DefaultFullHttpResponse createStatuHttpResponse(HttpResponseStatus responseStatus, byte[] bytes) {
		this.getContent().clear();
		this.getContent().writeBytes(bytes);
		DefaultFullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus,
				this.getContent());
		HttpHeaders headers = fullHttpResponse.headers();
		headers.set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
		headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
		headers.set(HttpHeaderNames.CONTENT_LENGTH, this.getContent().readableBytes());
		return fullHttpResponse;
	}
	  
	/**
	 * 创建500错误的http回复
	 * @return 500 error response
	 */
	public DefaultFullHttpResponse create500FullHttpResponse() {
		return this.createStatuHttpResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR,
				internalServerErrorBytes);
	}
	
	
}
