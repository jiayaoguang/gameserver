package com.jyg.net;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.GeneratedMessageV3;
import com.jyg.bean.LogicEvent;
import com.jyg.session.Session;
import com.jyg.timer.Timer;
import com.jyg.timer.TimerTrigger;

import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * created by jiayaoguang at 2017年12月17日
 * 用来分发给各个对应注册事件的的处理器
 */
public class EventDispatcher{

	private static final EventDispatcher dispatcher = new EventDispatcher();
	
	private final HttpProcessor notFOundProcessor = new NotFoundHttpProcessor();
	
	
	private final Long2ObjectMap<Channel> requestidTohttpChannelMap = new Long2ObjectOpenHashMap<>();
	
	public Long2ObjectMap<Channel> getRequestidTohttpChannelMap() {
		return requestidTohttpChannelMap;
	}
	
	private EventDispatcher() {

	}

	public static EventDispatcher getInstance() {
		return dispatcher;
	}

	private  final Int2ObjectMap< ProtoProcessor<? extends GeneratedMessageV3>> logicEventMap = new Int2ObjectOpenHashMap<>();
	private  final Map<String, HttpProcessor> httpPathMap = new HashMap<>();
	private  final Int2ObjectMap< ProtoProcessor<? extends GeneratedMessageV3>> socketEventMap = new Int2ObjectOpenHashMap<>();
	
	private final Object2IntMap<String> eventidToProtoNameMap = new Object2IntOpenHashMap<>();
	
	private final Map<Channel,Session> channelMap = new HashMap<>();
	
	/**
	 * 注册游戏逻辑事件
	 * @param eventid
	 * @param processor
	 * @throws Exception
	 */
	public void registerLogicEvent(int eventid, ProtoProcessor<? extends GeneratedMessageV3> processor) throws Exception {
		if(logicEventMap.containsKey(eventid)) {
			throw new Exception("dupilcated eventid");
		}
		logicEventMap.put(eventid, processor);
	}
	//================================ rpc start =========================================
	/**
	 * 注册普通socket事件
	 * @param eventid 事件id
	 * @param processor 事件处理器
	 */
	public void registerSocketEvent(int eventid, ProtoProcessor<? extends GeneratedMessageV3> processor) throws Exception {
		if(socketEventMap.containsKey(eventid)) {
			throw new Exception("dupilcated eventid");
		}
//		eventidToProtoNameMap.put(processor.getProtoClassName(), eventid);
		socketEventMap.put(eventid, processor);
	}
	
	public ProtoProcessor<? extends GeneratedMessageV3> getSocketProcessor(int id) {
		return socketEventMap.get(id);
	}
	
	/**
	 * 处理普通socket事件
	 */
	public void socketProcess(LogicEvent<? extends GeneratedMessageV3> event) throws Exception {
//		MessageLite msg = event.getData();
		ProtoProcessor processor = socketEventMap.get(event.getEventId());
		if(processor==null) {
			System.out.println("unknown socket eventid :" + event.getEventId());
			return;
		}
		processor.process((LogicEvent<? extends GeneratedMessageV3>) event);
	}

	
	public Integer getEventIdByProtoName(String protoName) {
		return eventidToProtoNameMap.get(protoName);
	}
	/**
	 * 绑定事件id到proto类，用于发送protobuf消息时，获得对应的eventId，并一起发送给对方
	 * @throws Exception 
	 */
	public void registerSendEventIdByProto(int eventId,Class<? extends GeneratedMessageV3> protoClazz) throws Exception {
		if(eventidToProtoNameMap.containsKey(protoClazz.getName())) {
			throw new Exception("dupilcated protoClazz");
		}
		eventidToProtoNameMap.put(protoClazz.getName(), eventId);
	}
	
	//================================ socket end =========================================
	
	private long uid = 1L;

	public void as_on_game_client_come(LogicEvent<Object> event) {
		channelMap.put(event.getChannel(),new Session(event.getChannel(), uid++ ));
		// event.getChannel().writeAndFlush(new TextWebSocketFrame(""));
	}

	public void as_on_game_client_leave(LogicEvent<Object> event) {
		channelMap.remove(event.getChannel());
	}

	public void webSocketProcess(LogicEvent<? extends GeneratedMessageV3> event) throws Exception {
		ProtoProcessor processor = socketEventMap.get(event.getEventId());
		if(processor==null) {
			System.out.println("unknown logic eventid");
			return;
		}
		processor.process(event);
//			eventTimes++;
//			if(eventTimes == 1000) {
//				eventTimes = 0;
//				dispatcher.loop();
//			}
	}

//	private int eventTimes = 0;
	
	TimerTrigger trigger = new TimerTrigger();
	
	public void addTimer(Timer timer) {
		trigger.addTimer(timer);
	}
	
	public void loop() {
		
		trigger.tickTigger();
		
	}
	//============================= http start ===========================================
	/**
	 * 注册http事件
	 * @param id
	 */
	public void registerHttpEvent(String path, HttpProcessor processor) throws Exception {
//		path = "/" + path;
		if(httpPathMap.containsKey(path)) {
			throw new Exception("dupilcated path");
		}
		if(path.charAt(0)!='/'||path.contains(".")) {
			throw new Exception("path cannot contain char:'.' and must start with char:'/' ");
		}
		
		httpPathMap.put(path, processor);
	}
	
	HttpProcessor getHttpProcessor(String path) {
		HttpProcessor processor = httpPathMap.get(path );
		if(processor==null) {
			processor = notFOundProcessor;
		}
		return processor;
	}
	
	public void httpProcess(LogicEvent<Request> event) throws Exception {
		getHttpProcessor(event.getData().noParamUri()).process(event);
	}
	
	
	
	@Deprecated
	public String getNoParamPath(String uri) {
		int endIndex = uri.indexOf('?');
		if(endIndex==-1) {
			return uri;
		}
		
		return uri.substring(0, endIndex);
	}
	//============================= http end ===========================================

}
