package com.jyg.handle;

import com.jyg.bean.LogicEvent;
import com.jyg.enums.EventType;
import com.jyg.net.EventDispatcher;
import com.jyg.net.Processor;
import com.jyg.util.GlobalQueue;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AsciiString;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' };

    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");

    EventDispatcher processor = EventDispatcher.getInstance();
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
        	long sequence = GlobalQueue.ringBuffer.next();
        	try {
        		
        		LogicEvent event = GlobalQueue.ringBuffer.get(sequence);
        		event.setData(msg);
        		event.setChannel(ctx.channel());
        		event.setChannelEventType(EventType.HTTP_MSG_COME);
        	}finally {
        		 GlobalQueue.ringBuffer.publish(sequence);
        	}
        	
        	
//            HttpRequest request = (HttpRequest) msg;
            
//            boolean keepAlive = HttpUtil.isKeepAlive(request);
            
//            HttpEvent event = new HttpEvent();
//            event.setUri(request.uri());
//            Map<String,String> params = RequestParser.parse((FullHttpRequest)request);
//            event.setData(params);
//            ByteBuf bytebuf = processor.process(event);
            
            
            
//            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(CONTENT));
//            response.headers().set(CONTENT_TYPE, "text/plain");
//            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
//            
//            
//            if (!keepAlive) {
//                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
//            } else {
//                response.headers().set(CONNECTION, KEEP_ALIVE);
//                ctx.write(response);
//            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
