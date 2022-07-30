package org.jyg.gameserver.core.session;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jyg.gameserver.core.data.RemoteInvokeData;
import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.core.msg.ByteMsgObj;

import java.util.List;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public abstract class Session implements Lifecycle {

	private final long sessionId;
	

	private long lastContactMill = 0;

	private SessionState sessionState;


	private Object sessionObject;
	
	public Session(long sessionId){
		this.sessionId = sessionId;
		setLastContactMill(System.currentTimeMillis());
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


	public long getLastContactMill() {
		return lastContactMill;
	}

	public void setLastContactMill(long lastContactMill) {
		this.lastContactMill = lastContactMill;
	}

	public void writeMessage( MessageLite message){
		this.writeObjMessage(message);
	}

	@Deprecated
	public void writeWsMessage(String message) {
		this.writeObjMessage(message);
	}

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


}

