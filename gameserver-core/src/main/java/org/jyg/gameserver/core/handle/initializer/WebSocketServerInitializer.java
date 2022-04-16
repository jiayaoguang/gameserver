package org.jyg.gameserver.core.handle.initializer;

/**
 * created by jiayaoguang at 2017年12月6日
 */
import io.netty.handler.codec.http.websocketx.WebSocket13FrameEncoder;
import org.jyg.gameserver.core.handle.WebSocketMsgDecoder;
import org.jyg.gameserver.core.handle.WebSocketMsgEncoder;
import org.jyg.gameserver.core.util.Context;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServerInitializer extends
		MyChannelInitializer<Channel> {

	public WebSocketServerInitializer(Context context) {
		super(context);
	}

	@Override
	public void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(64 * 1024));
		pipeline.addLast(new ChunkedWriteHandler());
//		pipeline.addLast(new HttpStaticFileServerHandler());
		
		pipeline.addLast(new WebSocketServerProtocolHandler("/"));
//		pipeline.addLast(new WebSocket13FrameEncoder(false));
//		pipeline.addLast(new WebSocketFrameDecoder(context));
		pipeline.addLast(new WebSocketMsgDecoder(context));
		pipeline.addLast(new WebSocketMsgEncoder(context));
	}
}