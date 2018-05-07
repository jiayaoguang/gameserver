package com.jyg.enums;


/**
 * created by jiayaoguang at 2018年4月9日
 */
public enum ProtoEnum {

	P_COMMON_REQUEST_PING(101),
	
	P_COMMON_RESPONSE_PONG(102),
	
	P_AUTH_SM_REQUEST_SEND_TOKEN(103),
	
	P_SM_AUTH_RESPONSE_RECEIVE_TOKEN(104),
	
	
//	P_AUTH_SM_REGISTER_ROLE(5),
//	
//	P_SM_AUTH_REGISTER_ROLE_SUCCESS(6),
	
	;
	
	private final int eventId;
	
	ProtoEnum(int eventId){
		this.eventId = eventId;
	}
	
	public int getEventId() {
		return eventId;
	}
}

