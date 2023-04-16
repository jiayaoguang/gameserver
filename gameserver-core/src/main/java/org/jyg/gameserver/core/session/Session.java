package org.jyg.gameserver.core.session;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public abstract class Session implements Lifecycle {

	private final long sessionId;
	

	private long lastSendMsgTime = 0;

	private SessionState sessionState;


	private Object sessionObject;


	private int sessionType;
	
	public Session(long sessionId){
		this.sessionId = sessionId;
	}

	public long getSessionId() {
		return sessionId;
	}

	/*public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}*/

	public Channel getChannel() {
		return null;
	}


	public abstract boolean isOpen();


	public long getLastSendMsgTime() {
		return lastSendMsgTime;
	}

	public void setLastSendMsgTime(long lastSendMsgTime) {
		this.lastSendMsgTime = lastSendMsgTime;
	}

	public void writeMessage( MessageLite message){
		this.writeObjMessage(message);
	}

//	@Deprecated
//	public void writeWsMessage(String message) {
//		this.writeObjMessage(message);
//	}

	public void writeMessage( MessageLite.Builder messageBuilder) {
		this.writeObjMessage(messageBuilder.build());
	}



	public void writeMessage(ByteMsgObj byteMsgObj){
		this.writeObjMessage(byteMsgObj);
	}


	protected abstract void writeObjMessage(Object msgObj);


	public abstract String getRemoteAddr();



	public SessionState getSessionState() {
		return sessionState;
	}

	public void setSessionState(SessionState sessionState) {
		this.sessionState = sessionState;
	}


	@SuppressWarnings("unchecked")
	public <T> T getSessionObject() {
		return (T)sessionObject;
	}

	public void setSessionObject(Object sessionObject) {
		this.sessionObject = sessionObject;
	}



	public int getSessionType() {
		return sessionType;
	}

	public void setSessionType(int sessionType) {
		this.sessionType = sessionType;
	}
}

