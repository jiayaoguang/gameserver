package org.jyg.gameserver.core.net;

import java.util.ArrayList;
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
	
	
	private String contentType =  "text/html; charset=UTF-8";
	
	public static final String CONTENT_TYPE_JSON = "application/json";
	
	public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
	
	//要发给客户端的cookis
	private final List<Cookie> cookies = new ArrayList<>(3);
	
	// 500错误提示信息
	private static final byte[] internalServerErrorBytes = 
			"<html><head></head><body><div align='center'><h1>500 Internal Server Error</h1></div><body></html>"
			.getBytes();

	private static final String SENDR_EDIRECT_HTML = "<html><head> <meta http-equiv=\"refresh\" content=0;URL=%s /> </head></html>";


	private final Channel channel;
	
	public Response(Channel channel) {
		this.channel = channel;
	}
	
	
	public Channel getChannel() {
		return channel;
	}


	public void writeAndFlush(String msg) {
		this.writeAndFlush(msg.getBytes());
	}
	
	public void writeAndFlush(byte[] bs) {
		if(bs == null){
			write500Error();
			return;
		}
		ByteBuf content = this.createContent(bs.length);
		content.writeBytes(bs);
		this.getChannel().writeAndFlush(this.createDefaultFullHttpResponse(content));
	}
	
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	


	private ByteBuf createContent(int initialCapacity) {
		return Unpooled.buffer(initialCapacity);
	}
	
	
	
	/**
	 * 添加一个cookie
	 * @param cookie cookie
	 */
	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}
	
	// ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
	/**
	 * 创建一个返回给客户端的回复
	 * @return 给客户端的回复
	 */
	private DefaultFullHttpResponse createDefaultFullHttpResponse(ByteBuf content) {
		DefaultFullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
				HttpResponseStatus.OK, content);
		HttpHeaders headers = fullHttpResponse.headers();
		headers.set(HttpHeaderNames.CONTENT_TYPE, this.contentType);
		headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
		headers.set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
		headers.set(HttpHeaderNames.CONTENT_ENCODING, CharsetUtil.UTF_8);
		// cookie
//		Cookie cookie = new DefaultCookie("jygsessionid", genericSessionId());
//		cookie.setMaxAge(60*60);
//		//cookie.setDomain(System.getenv("USERDOMAIN"));
//		cookie.setPath("/");
//		cookies.add(cookie);
		if(!cookies.isEmpty()) {
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
		ByteBuf content = this.createContent(bytes.length).writeBytes(bytes);
		DefaultFullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus,
				content);
		HttpHeaders headers = fullHttpResponse.headers();
		headers.set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
		headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
		headers.set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
		return fullHttpResponse;
	}
	  
	/**
	 * 创建500错误的http回复
	 * @return 500 error response
	 */
	private DefaultFullHttpResponse create500FullHttpResponse() {
		return this.create500FullHttpResponse(internalServerErrorBytes);
	}

	/**
	 * 创建500错误的http回复
	 * @return 500 error response
	 */
	private DefaultFullHttpResponse create500FullHttpResponse(String msg) {
		return this.createStatuHttpResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR,
				msg.getBytes(cn.hutool.core.util.CharsetUtil.CHARSET_UTF_8));
	}
	/**
	 * 创建500错误的http回复
	 * @return 500 error response
	 */
	private DefaultFullHttpResponse create500FullHttpResponse(byte[] byteMsg) {
		return this.createStatuHttpResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR,
				byteMsg);
	}

	public void sendRedirect(String path){

		String formatContent = String.format(SENDR_EDIRECT_HTML , path);

		this.writeAndFlush(formatContent);
	}
	
	public void write500Error() {
		this.channel.writeAndFlush( create500FullHttpResponse() );
	}

	public void write500Error(String errorMsg) {

		String sb = "<html><head></head><body><div align='center'><h1>500 Internal Server Error</h1></div>" +
				"<details class=\"menu\" > <summary>errorMsg</summary> <ul><li>" +
				"<pre>" +
				errorMsg +
				"</pre>" +
				"</li>" +
				"</ul>\n" +
				"</details>" +
				"<body></html>";

		this.channel.writeAndFlush(create500FullHttpResponse(sb));


	}

	
}
