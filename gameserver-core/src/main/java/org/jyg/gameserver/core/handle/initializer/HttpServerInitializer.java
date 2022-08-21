package org.jyg.gameserver.core.handle.initializer;

import org.jyg.gameserver.core.handle.HttpServerHandler;
import org.jyg.gameserver.core.handle.HttpStaticFileServerHandler;

/**
 * created by jiayaoguang at 2017年12月6日
 */
import org.jyg.gameserver.core.util.GameContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends MyChannelInitializer<Channel> {
	
	//是否是同步的http
//	private boolean isSynHttp;

	public HttpServerInitializer(GameContext gameContext) {
		super(gameContext);
//		isSynHttp = true;
	}

//	public HttpServerInitializer(boolean isSynHttp) {
//		this.isSynHttp = isSynHttp;
//	}
//
//	public boolean isSynHttp() {
//		return isSynHttp;
//	}
	//TODO
//	private final HttpServerCodec httpCodec= new HttpServerCodec();
	
	@Override
	public void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
//		pipeline.addLast(new InnerSocketHandler());
		pipeline.addLast( new HttpServerCodec());
		// 处理过长的请求
		pipeline.addLast(new HttpObjectAggregator(64 * 1024));
		// pipeline.addLast(new ChunkedWriteHandler());//主要用于处理大数据流,比如一个1G大小的文件
		pipeline.addLast(new HttpStaticFileServerHandler());
		pipeline.addLast(new HttpServerHandler(gameContext));

	}
}