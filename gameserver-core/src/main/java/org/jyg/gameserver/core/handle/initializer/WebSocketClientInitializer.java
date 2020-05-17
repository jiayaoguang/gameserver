package org.jyg.gameserver.core.handle.initializer;

import org.jyg.gameserver.core.handle.TextWebSocketFrameHandler;

/**
 * created by jiayaoguang at 2017年12月6日
 */
import org.jyg.gameserver.core.util.Context;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class WebSocketClientInitializer extends
		MyChannelInitializer<SocketChannel> {

	protected WebSocketClientInitializer(Context context) {
		super(context);
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(64 * 1024));
//		pipeline.addLast(new ChunkedWriteHandler());
//		pipeline.addLast(new HttpStaticFileServerHandler());
		
		//WebSocketClientProtocolHandler TODO
		pipeline.addLast(new WebSocketServerProtocolHandler("/"));
		pipeline.addLast(new TextWebSocketFrameHandler(context.getGlobalQueue()));
		
	}
}