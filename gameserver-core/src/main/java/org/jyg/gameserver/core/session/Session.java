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
public class Session implements Lifecycle {

	private final long sessionId;
	
	private Channel channel;
	
	private long lastContactMill = 0;

	private SessionState sessionState;


	private Object sessionObject;
	
	public Session(Channel channel,long sessionId){
		this.channel = channel;
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
		return channel;
	}


	public long getLastContactMill() {
		return lastContactMill;
	}

	public void setLastContactMill(long lastContactMill) {
		this.lastContactMill = lastContactMill;
	}

	public void writeMessage( MessageLite message) {
		this.channel.writeAndFlush(message);
	}

	public void writeWsMessage(String message) {
		this.channel.writeAndFlush(new TextWebSocketFrame(message));
	}

	public void writeMessage( MessageLite.Builder messageBuider) {
		this.channel.writeAndFlush(messageBuider.build());
	}

	public void writeMessage( List<? extends MessageLite> messageList) {
		this.channel.writeAndFlush(messageList);
	}


	public void writeMessage(RemoteInvokeData remoteInvokeData){
		this.channel.writeAndFlush(remoteInvokeData);
	}

	public void writeMessage(ByteMsgObj byteMsgObj){
		this.channel.writeAndFlush(byteMsgObj);
	}

	@Override
	public void start() {

	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public void stop() {
		if(channel != null){
			try{
				channel.close();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}


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

