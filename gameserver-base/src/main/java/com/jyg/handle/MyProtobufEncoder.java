package com.jyg.handle;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.jyg.bean.EventIdAndMessageLiteOrBuilder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * created by jiayaoguang at 2018年3月13日
 */
public class MyProtobufEncoder extends MessageToMessageEncoder<EventIdAndMessageLiteOrBuilder> {
    @Override
    protected void encode(ChannelHandlerContext ctx, EventIdAndMessageLiteOrBuilder eventIdAndMsg, List<Object> out)
            throws Exception {
    	MessageLiteOrBuilder msg = eventIdAndMsg.messageLiteOrBuilder;
    	byte[] bytes = null;
        if (msg instanceof MessageLite) {
        	bytes = ((MessageLite) msg).toByteArray();
        }
        else if (msg instanceof MessageLite.Builder) {
        	bytes = ((MessageLite.Builder) msg).build().toByteArray();
        }
        
        if(bytes == null) {
        	System.out.println("not MessageLiteOrBuilder");
        	return;
        }
        
        ByteBuf buf = ctx.alloc().directBuffer(4 + bytes.length);
        buf.writeInt(bytes.length);
        buf.writeInt(eventIdAndMsg.eventid);
        buf.writeBytes(bytes);
        out.add(buf);
    }
}
