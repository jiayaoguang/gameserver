package org.jyg.gameserver.core.session;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class Session {

	private final int sessionId;
	
	private Channel channel;
	
	private long lastContactMill = 0;
	
	private Object data;
	
	public Session(Channel channel,int sessionId){
		this.channel = channel;
		this.sessionId = sessionId;
		setLastContactMill(System.currentTimeMillis());
	}

	public int getSessionId() {
		return sessionId;
	}

	/*public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}*/

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
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
	
	
}

