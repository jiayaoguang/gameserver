package com.jyg.enums;


/**
 * created by jiayaoguang at 2018年4月9日
 */
public enum ProtoEnum {

	P_AUTH_SM_REQUEST_SEND_TOKEN(1),
	
	P_SM_AUTH_RESPONSE_RECEIVE_TOKEN(2),
	
	;
	
	private final int eventId;
	
	ProtoEnum(int eventId){
		this.eventId = eventId;
	}
	
	public int getEventId() {
		return eventId;
	}
}

