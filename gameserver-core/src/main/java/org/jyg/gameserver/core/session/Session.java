package org.jyg.gameserver.core.session;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.util.Logs;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public abstract class Session implements Lifecycle {

	private final long sessionId;
	

//	private long lastSendMsgTime = 0;

	private SessionState sessionState;


	private Object sessionObject;


	private int sessionType;


//	private Queue<Long> recentReceiveMsgTimeQueue = new ArrayDeque<>(1024);

	private Int2LongMap lastReceiveMsgTimeMap = new Int2LongOpenHashMap(1024);

	
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


//	public long getLastSendMsgTime() {
//		return lastSendMsgTime;
//	}
//
//	public void setLastSendMsgTime(long lastSendMsgTime) {
//		this.lastSendMsgTime = lastSendMsgTime;
//	}

	public void writeMessage( MessageLite message){

		try{
			this.writeObjMessage(message);
		}catch (Exception e){
			Logs.DEFAULT_LOGGER.error("writeMessage Exception : " , e);
		}
	}

//	@Deprecated
//	public void writeWsMessage(String message) {
//		this.writeObjMessage(message);
//	}

	public void writeMessage( MessageLite.Builder messageBuilder) {
		try{
			this.writeObjMessage(messageBuilder.build());
		}catch (Exception e){
			Logs.DEFAULT_LOGGER.error("writeMessage Exception : " , e);
		}

	}


//	protected void recordReceiveMessageTime(){
//		long now = System.currentTimeMillis();
//
//		recentReceiveMsgTimeQueue.offer(now);
//
//		long expireRecordTime = now - TimeUnit.MINUTES.toMillis(1);
//
//		for(;!recentReceiveMsgTimeQueue.isEmpty() && recentReceiveMsgTimeQueue.peek() > expireRecordTime;){
//			recentReceiveMsgTimeQueue.poll();
//		}
//
//	}


	public void recordReceiveMsgTime(int msgId){
		long now = System.currentTimeMillis();
		lastReceiveMsgTimeMap.put(msgId , now);
	}


	public long getMsgLastReceiveTime(int msgId){
		return lastReceiveMsgTimeMap.getOrDefault(msgId,0L);
	}


	public void writeMessage(ByteMsgObj byteMsgObj){
		try{
			this.writeObjMessage(byteMsgObj);
		}catch (Exception e){
			Logs.DEFAULT_LOGGER.error("writeMessage Exception : " , e);
		}
	}


	protected abstract void writeObjMessage(Object msgObj) throws Exception;


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

