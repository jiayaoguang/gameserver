package com.jyg.bean;

import com.jyg.enums.EventType;
import com.jyg.handle.TextWebSocketFrameHandler;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2017年12月6日
 * @param <T>
 */
public class LogicEvent{

	private Channel channel;
	
	private EventType channelEventType;

	private Object data;
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
		
//		TextWebSocketFrameHandler.channels.add(channel);
	}

	public EventType getChannelEventType() {
		return channelEventType;
	}

	public void setChannelEventType(EventType channelEventType) {
		this.channelEventType = channelEventType;
	}

	
	public Object getData() {
		// TODO Auto-generated method stub
		return data;
	}

	
	public void setData(Object data) {
		this.data = data;
		
	}

	
}

