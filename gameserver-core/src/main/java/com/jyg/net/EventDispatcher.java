package com.jyg.net;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.MessageLite;
import com.jyg.bean.LogicEvent;
import com.jyg.manager.ChannelManager;
import com.jyg.processor.HttpProcessor;
import com.jyg.processor.NotFoundHttpProcessor;
import com.jyg.processor.ProtoProcessor;
import com.jyg.session.Session;
import com.jyg.timer.Timer;
import com.jyg.timer.TimerManager;
import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * created by jiayaoguang at 2017年12月17日
 * 用来分发给各个对应注册事件的的处理器
 */
public class EventDispatcher {

	private final HttpProcessor notFOundProcessor = new NotFoundHttpProcessor();

	private final Map<String, HttpProcessor> httpPathMap = new HashMap<>();
	private final Int2ObjectMap<ProtoProcessor<? extends GeneratedMessageV3>> socketEventMap = new Int2ObjectOpenHashMap<>();

	private final Object2IntMap<Class<? extends MessageLite>> protoClazzToEventidMap = new Object2IntOpenHashMap<>();

	private final ChannelManager channelManager = new ChannelManager();

	//50 毫秒一帧
	private static final long FRAME_DURATION_TIMEMILLS = 50L;
	//上一帧时间戳
	private long nextFrameTimeStamp = System.currentTimeMillis();

	private TimerManager timerManager;

	public EventDispatcher() {

	}

	public void init( TimerManager timerManager ){
		this.timerManager = timerManager;
		timerManager.addTimer(new Timer(Integer.MAX_VALUE, 60 * 1000L, 60 * 1000L) {
			public void onTime() {
				channelManager.removeOutOfTimeChannels();
			}
		});
	}


	//================================ rpc start =========================================

	/**
	 * 注册普通socket事件
	 *
	 * @param eventid   事件id
	 * @param processor 事件处理器
	 */
	public void registerSocketEvent(int eventid, ProtoProcessor<? extends GeneratedMessageV3> processor) {
		if (socketEventMap.containsKey(eventid)) {
			throw new IllegalArgumentException("dupilcated eventid");
		}
//		eventidToProtoNameMap.put(processor.getProtoClassName(), eventid);
		processor.setEventDispatcher(this);
		socketEventMap.put(eventid, processor);
	}

	public ProtoProcessor<? extends GeneratedMessageV3> getSocketProcessor(int id) {
		return socketEventMap.get(id);
	}

	/**
	 * 处理普通socket事件
	 */
	public void socketProcess(LogicEvent<? extends GeneratedMessageV3> event) {
//		MessageLite msg = event.getData();
		ProtoProcessor processor = socketEventMap.get(event.getEventId());
		if (processor == null) {
			System.out.println("unknown socket eventid :" + event.getEventId());
			return;
		}
		processor.process((LogicEvent<? extends GeneratedMessageV3>) event);
	}


	public int getEventIdByProtoClazz(Class<? extends MessageLite> protoClazz) {
		return protoClazzToEventidMap.getOrDefault(protoClazz,-1);
	}

	/**
	 * 绑定事件id到proto类，用于发送protobuf消息时，获得对应的eventId，并一起发送给对方
	 *
	 * @throws Exception
	 */
	public void registerSendEventIdByProto(int eventId, Class<? extends MessageLite> protoClazz) throws Exception {
		if (protoClazzToEventidMap.containsKey(protoClazz)) {
			throw new IllegalArgumentException("dupilcated protoClazz:" + protoClazz.getName());
		}
		protoClazzToEventidMap.put(protoClazz ,eventId);
	}

	//================================ socket end =========================================

	public void as_on_client_active(LogicEvent<Object> event) {
		channelManager.doLink(event);
		// event.getChannel().writeAndFlush(new TextWebSocketFrame(""));
	}

	public void as_on_client_inactive(LogicEvent<Object> event) {
		channelManager.doUnlink(event);
	}

	public void as_on_inner_server_active(LogicEvent<Object> event) {
		InetSocketAddress address = (InetSocketAddress) event.getChannel().remoteAddress();

		System.out.println("name connect:" + address.getHostString());

	}

	public void as_on_inner_server_inactive(LogicEvent<Object> event) {
		InetSocketAddress address = (InetSocketAddress) event.getChannel().remoteAddress();

		System.out.println("name disconnect:" + address.getHostString());

	}

	public Session getSession(Channel channel) {
		return channelManager.getSession(channel);
	}




	public void webSocketProcess(LogicEvent<? extends GeneratedMessageV3> event) {
		ProtoProcessor processor = socketEventMap.get(event.getEventId());
		if (processor == null) {
			System.out.println("unknown logic eventid");
			return;
		}
		processor.process(event);
	}


//	public void addTimer(Timer timer) {
//		timerManager.addTimer(timer);
//	}

	public void loop() {

//		timerManager.updateTimer();
		updateFrame();
	}

	/**
	 * 暂时这么写
	 */
	private void updateFrame() {
		long now = System.currentTimeMillis();
		while (now >= nextFrameTimeStamp) {
			nextFrameTimeStamp += FRAME_DURATION_TIMEMILLS;

			//TODO logic code
		}
	}

	//============================= http start ===========================================

	/**
	 * 注册http事件
	 *
	 * @param path http url path
	 */
	public void registerHttpEvent(String path, HttpProcessor processor) throws Exception {
//		path = "/" + path;
		if (httpPathMap.containsKey(path)) {
			throw new IllegalArgumentException("dupilcated path");
		}
		if (path.charAt(0) != '/' || path.contains(".")) {
			throw new IllegalArgumentException("path cannot contain char:'.' and must start with char:'/' ");
		}

		httpPathMap.put(path, processor);
		processor.setEventDispatcher(this);
	}

	public HttpProcessor getHttpProcessor(String path) {
		HttpProcessor processor = httpPathMap.get(path);
		if (processor == null) {
			processor = notFOundProcessor;
		}
		return processor;
	}

	public void httpProcess(LogicEvent<Request> event) {
		getHttpProcessor(event.getData().noParamUri()).process(event);
	}


	@Deprecated
	public String getNoParamPath(String uri) {
		int endIndex = uri.indexOf('?');
		if (endIndex == -1) {
			return uri;
		}

		return uri.substring(0, endIndex);
	}
	//============================= http end ===========================================


}
