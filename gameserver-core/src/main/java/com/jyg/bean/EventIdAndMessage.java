package com.jyg.bean;

import com.google.protobuf.MessageLiteOrBuilder;

/**
 * created by jiayaoguang at 2018年3月26日
 */
public class EventIdAndMessage {

	public final int eventId;
	public final MessageLiteOrBuilder message;
	public EventIdAndMessage(int eventId, MessageLiteOrBuilder message) {
		super();
		this.eventId = eventId;
		this.message = message;
	}
	
}

