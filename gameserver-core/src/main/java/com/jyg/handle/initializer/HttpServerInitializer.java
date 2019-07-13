package com.jyg.handle.initializer;

import com.jyg.handle.InnerSocketHandler;
import com.jyg.handle.SyncHttpServerHandler;
import com.jyg.handle.HttpStaticFileServerHandler;

/**
 * created by jiayaoguang at 2017年12月6日
 */
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

public class HttpServerInitializer extends ChannelInitializer<Channel> {
	
	//是否是同步的http
//	private boolean isSynHttp;

	public HttpServerInitializer() {
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
		pipeline.addLast(new SyncHttpServerHandler());

	}
}