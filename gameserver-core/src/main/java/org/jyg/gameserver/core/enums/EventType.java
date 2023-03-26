package org.jyg.gameserver.core.enums;

/**
 * created by jiayaoguang at 2017年12月12日
 */
public enum EventType {

	UNKNOWN(0),


	/**
	 * 连接激活
	 */
	@Deprecated
	SOCKET_CONNECT_ACTIVE(3),

	/**
	 * 连接断开
	 */
	@Deprecated
	SOCKET_CONNECT_INACTIVE(4),
	
	CLIENT_SOCKET_CONNECT_ACTIVE(5),
	
	CLIENT_SOCKET_CONNECT_INACTIVE(6),
	
//	ON_MESSAGE_COME,

	@Deprecated
	HTTP_MESSAGE_COME(7),

    REMOTE_MSG_COME(8),

	MQ_MSG_COME(9),




	DEFAULT_EVENT(12),
	/**
	 * 回调事件
	 */
	RESULT_CALL_BACK(13),


	LOCAL_MSG_COME(14),

	/**
	 * 未知数据
	 */
	REMOTE_UNKNOWN_MSG_COME(15),


	PUBLISH_EVENT(16),


	;

	public final int type;

	EventType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
}

