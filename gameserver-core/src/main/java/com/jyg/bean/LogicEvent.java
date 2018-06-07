package com.jyg.bean;

import com.jyg.enums.EventType;
import com.jyg.handle.TextWebSocketFrameHandler;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2017年12月6日
 * @param <T>
 */
public class LogicEvent<T>{

	private Channel channel;
	
	private EventType channelEventType;

	private T data;
	
	
	//http用不到
	private int eventId;
	
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

	
	public T getData() {
		// TODO Auto-generated method stub
		return data;
	}

	
	public void setData(T data) {
		this.data = data;
		
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	
	@Override
	public String toString() {
		return ""+channelEventType+","+this.eventId+data;
	}
	
}

