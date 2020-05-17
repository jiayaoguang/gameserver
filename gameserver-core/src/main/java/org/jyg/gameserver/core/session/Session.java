package org.jyg.gameserver.core.session;

import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.channel.Channel;

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

	public void writeMessage( MessageLiteOrBuilder message) {
		this.channel.writeAndFlush(message);
	}
	
	
}

