package org.jyg.gameserver.core.enums;
/**
 * created by jiayaoguang at 2017年12月12日
 */
public enum EventType {

	ACTIVE,
	INACTIVE,

	/**
	 * 连接激活
	 */
	SOCKET_CONNECT_ACTIVE,

	/**
	 * 连接断开
	 */
	SOCKET_CONNECT_INACTIVE,
	
	CLIENT_SOCKET_CONNECT_ACTIVE,
	
	CLIENT_SOCKET_CONNECT_INACTIVE,
	
//	ON_MESSAGE_COME,

	HTTP_MESSAGE_COME,

	PROTO_MSG_COME,


	TEXT_MESSAGE_COME,


	BYTE_OBJ_MSG_COME,

	INNER_MSG,


	DEFAULT_EVENT,

	CALL_BACK,

	;
	
}

