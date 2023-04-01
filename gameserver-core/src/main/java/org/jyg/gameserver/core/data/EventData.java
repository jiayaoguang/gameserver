package org.jyg.gameserver.core.data;


import io.netty.channel.Channel;
import org.jyg.gameserver.core.event.Event;

import java.util.Map;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class EventData<T>{


	private Event event;
	
	
	//http用不到
	private int eventId;


	public String childChooseId;

	public Map<String, Object> params;


	public EventData() {
	}

	

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}



	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}



	public String getChildChooseId() {
		return childChooseId;
	}

	public void setChildChooseId(String childChooseId) {
		this.childChooseId = childChooseId;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
}

