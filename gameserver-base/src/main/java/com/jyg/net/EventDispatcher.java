package com.jyg.net;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.Parser;
import com.jyg.bean.LogicEvent;
import com.jyg.session.Session;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

/**
 * created by jiayaoguang at 2017年12月17日
 * 用来分发给各个对应注册事件的的处理器
 */
public class EventDispatcher{

	private static final EventDispatcher dispatcher = new EventDispatcher();
	
	private final HttpProcessor notFOundProcessor = new NotFoundHttpProcessor();
	
	private EventDispatcher() {

	}

	public static EventDispatcher getInstance() {
		return dispatcher;
	}

	private  final Map<Integer, ProtoProcessor> logicEventMap = new HashMap<>();
	private  final Map<String, HttpProcessor> httpPathMap = new HashMap<>();
	private  final Map<Integer, ProtoProcessor> rpcEventMap = new HashMap<>();
	
	private final Map<Channel,Session> channelMap = new HashMap<>();
	
	/**
	 * 注册游戏逻辑事件
	 * @param eventid
	 * @param processor
	 * @throws Exception
	 */
	public void registerLogicEvent(int eventid, ProtoProcessor processor) throws Exception {
		if(logicEventMap.containsKey(eventid)) {
			throw new Exception("dupilcated eventid");
		}
		logicEventMap.put(eventid, processor);
	}
	
	/**
	 * 注册rpc事件
	 * @param eventid
	 * @param processor
	 * @throws Exception
	 */
	public void registerRpcEvent(int eventid, ProtoProcessor processor) throws Exception {
		if(rpcEventMap.containsKey(eventid)) {
			throw new Exception("dupilcated eventid");
		}
		rpcEventMap.put(eventid, processor);
	}
	
	public ProtoProcessor getRpcProcessor(int id) {
		return rpcEventMap.get(id);
	}
	
	/**
	 * 注册http事件
	 * @param id
	 * @return
	 */
	public void registerHttpEvent(String path, HttpProcessor processor) throws Exception {
		path = "/" + path;
		if(httpPathMap.containsKey(path)) {
			throw new Exception("dupilcated path");
		}
		httpPathMap.put(path, processor);
	}
	
	private long uid = 1L;

	public void as_on_game_client_come(LogicEvent<Object> event) {
		channelMap.put(event.getChannel(),new Session(event.getChannel(), uid++ ));
		// event.getChannel().writeAndFlush(new TextWebSocketFrame(""));
	}

	public void as_on_game_client_leave(LogicEvent<Object> event) {
		channelMap.remove(event.getChannel());
	}

	public void webSocketProcess(LogicEvent<ByteBuf> event) throws Exception {
		ByteBuf buf = event.getData();
		int eventid = buf.readInt();
		Processor<ByteBuf> processor = logicEventMap.get(eventid);
		if(processor==null) {
			System.out.println("unknown logic eventid");
			return;
		}
		processor.process(event);
			eventTimes++;
			if(eventTimes == 1000) {
				eventTimes = 0;
				dispatcher.loop();
			}
	}

	private int eventTimes = 0;
	
	public void loop() {
		
		
	}
	
	public void httpProcess(LogicEvent<Request> event) throws Exception {
		Request request = event.getData();
		String realPath = this.getNoParamPath( request.uri() );
		System.out.println(realPath);
		HttpProcessor processor = httpPathMap.get(realPath );
		if(processor==null) {
			processor = notFOundProcessor;
		}
		processor.process(event);
	}
	
	
	public String getNoParamPath(String uri) {
		int endIndex = uri.indexOf('?');
		if(endIndex==-1) {
			return uri;
		}
		
		return uri.substring(0, endIndex);
	}
	

}
