package org.jyg.gameserver.core.data;

import org.jyg.gameserver.core.enums.EventType;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class EventData<T>{

	public static final EventExtData EMPTY_EVENT_EXT_DATA = new EventExtData(0,0 , null);

	private Channel channel;
	
	private EventType eventType;

	private T data;

	private int fromConsumerId;

	private EventExtData eventExtData = EMPTY_EVENT_EXT_DATA;
	
	
	//http用不到
	private int eventId;
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
		
//		TextWebSocketFrameHandler.channels.add(channel);
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
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
		return eventType + "," + this.eventId + data;
	}

	public int getFromConsumerId() {
		return eventExtData.fromConsumerId;
	}


	public EventExtData getEventExtData() {
		return eventExtData;
	}

	public void setEventExtData(EventExtData eventExtData) {
		if(eventExtData == null){
			eventExtData = EMPTY_EVENT_EXT_DATA;
		}
		this.eventExtData = eventExtData;
	}
}

