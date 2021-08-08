package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import io.netty.util.CharsetUtil;

/**
 * created by jiayaoguang at 2018年3月21日
 */
public class NotFoundHttpProcessor extends HttpProcessor {
//	private HttpResponse notFoundResponse;

//	private ByteBuf bytebuf;

	private static final String NOT_FIND_HTML_TEXT = "<html><head></head><body>" +
			"<div align='center'><h1>404 not found </h1></div>" +
			"<body></html>";

	private final byte[] bytes;


	public NotFoundHttpProcessor() {
		this(NOT_FIND_HTML_TEXT);
	}

	public NotFoundHttpProcessor(String htmlText) {
		super("404");
		bytes = htmlText.getBytes(CharsetUtil.UTF_8);
//		bytebuf = Unpooled.wrappedBuffer(bytes);
//		notFoundResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, bytebuf);
	}

//	public void process(LogicEvent<HttpRequest> event) {
//		bytebuf = Unpooled.wrappedBuffer(bytes);
//		notFoundResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, bytebuf);
//		event.getChannel().writeAndFlush(notFoundResponse);
////		.addListener(ChannelFutureListener.CLOSE);
//		
//	}
	
	

	@Override
	public void service(Request request, Response response) {
		response.writeAndFlush(bytes);
	}

}
