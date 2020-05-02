package com.jyg.handle.initializer;

import com.jyg.handle.TextWebSocketFrameHandler;

/**
 * created by jiayaoguang at 2017年12月6日
 */
import com.jyg.util.IGlobalQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServerInitializer extends
		MyChannelInitializer<Channel> {

	public WebSocketServerInitializer(IGlobalQueue globalQueue) {
		super(globalQueue);
	}

	@Override
	public void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(64 * 1024));
		pipeline.addLast(new ChunkedWriteHandler());
//		pipeline.addLast(new HttpStaticFileServerHandler());
		
		pipeline.addLast(new WebSocketServerProtocolHandler("/"));
		pipeline.addLast(new TextWebSocketFrameHandler(globalQueue));
		
	}
}