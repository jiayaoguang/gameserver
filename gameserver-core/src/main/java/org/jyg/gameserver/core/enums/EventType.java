package org.jyg.gameserver.core.enums;
/**
 * created by jiayaoguang at 2017年12月12日
 */
public enum EventType {

	ACTIVE,
	INACTIVE,
	
	//服务器之间的连接激活
	SOCKET_CONNECT_ACTIVE,
	//服务器之间的连接断开
	SOCKET_CONNECT_INACTIVE,
	
	CLIENT_SOCKET_CONNECT_ACTIVE,
	
	CLIENT_SOCKET_CONNECT_INACTIVE,
	
	ON_MESSAGE_COME,
	
	HTTP_MSG_COME,
	
	RPC_MSG_COME,
	
	
	ON_TEXT_MESSAGE_COME,


	INNER_MSG,
	
	;
	
}

