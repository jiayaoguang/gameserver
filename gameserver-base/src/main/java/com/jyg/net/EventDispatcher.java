package com.jyg.net;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.Parser;
import com.jyg.bean.LogicEvent;
import com.jyg.session.Session;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2017年12月17日
 * 用来分发给各个对应注册事件的的处理器
 */
public class EventDispatcher{

	private static final EventDispatcher dispatcher = new EventDispatcher();

	private EventDispatcher() {

	}

	public static EventDispatcher getInstance() {
		return dispatcher;
	}

	private  final Map<Integer, Processor> logicEventMap = new HashMap<>();
	private  final Map<String, Processor> httpPathMap = new HashMap<>();
	private  final Map<Integer, ProtoProcessor> rpcEventMap = new HashMap<>();
	
	private final Map<Channel,Session> channelMap = new HashMap<>();
	
	//注册rpc事件
	public void registerLogicEvent(int eventid, Processor processor) throws Exception {
		if(logicEventMap.containsKey(eventid)) {
			throw new Exception("dupilcated eventid");
		}
		logicEventMap.put(eventid, processor);
	}
	
	
	public void registerRpcEvent(int eventid, ProtoProcessor processor) throws Exception {
		if(rpcEventMap.containsKey(eventid)) {
			throw new Exception("dupilcated eventid");
		}
		rpcEventMap.put(eventid, processor);
	}
	
	public ProtoProcessor getRpcProcessor(int i) {
		return rpcEventMap.get(i);
	}
	
	private long uid = 1L;

	public void as_on_game_client_come(LogicEvent event) {
		channelMap.put(event.getChannel(),new Session(event.getChannel(), uid++ ));
		// event.getChannel().writeAndFlush(new TextWebSocketFrame(""));
	}

	public void as_on_game_client_leave(LogicEvent event) {
		channelMap.remove(event.getChannel());
	}

	public ByteBuf webSocketProcess(LogicEvent event) throws Exception {
		ByteBuf buf = (ByteBuf) event.getData();
		int eventid = buf.readInt();
		Processor processor = logicEventMap.get(eventid);
		processor.process(event);
		eventTimes++;
		if(eventTimes == 1000) {
			eventTimes = 0;
			dispatcher.loop();
		}
		return buf;
	}

	int eventTimes = 0;
	public void loop() {
		
		
	}
	
	public void httpProcess(LogicEvent event) {
		
	}

}
