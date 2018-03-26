package com.jyg.net;
/**
 * created by jiayaoguang at 2018年3月26日
 */

import com.google.protobuf.MessageLiteOrBuilder;
import com.jyg.bean.EventIdAndMessage;

import io.netty.channel.Channel;

public class ProtoResponse {
	
	private Channel channel;
	
	public ProtoResponse(Channel channel) {
		this.channel = channel;
	}
	
	
	public void writeMsg( MessageLiteOrBuilder message) {
		this.channel.writeAndFlush(message);
		
	}
	

}

