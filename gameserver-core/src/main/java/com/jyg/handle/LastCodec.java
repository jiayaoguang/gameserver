package com.jyg.handle;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

/**
 * created by jiayaoguang at 2018年3月24日
 * 测试用，添加在管道尾部的编解码器
 */
public class LastCodec extends MessageToMessageCodec<Object, Object>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		System.out.println(msg.getClass().getName() + " >>>> msg write");
		out.add(msg);
		
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		System.out.println(msg.getClass().getName() + "<<<< msg read");
		out.add(msg);
	}

}

