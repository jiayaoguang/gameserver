package com.jyg.consumers;

import java.util.HashMap;
import java.util.Map;

import com.jyg.bean.LogicEvent;
import com.jyg.enums.EventType;
import com.jyg.net.Processor;
import com.jyg.net.EventDispatcher;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class EventConsumer implements EventHandler<LogicEvent>, WorkHandler<LogicEvent> {


	private final EventDispatcher process = EventDispatcher.getInstance();
	

	public EventConsumer() {

	}

	public void onEvent(LogicEvent event, long sequence, boolean endOfBatch) throws Exception {
		this.onEvent(event);
	}

	public void onEvent(LogicEvent event) throws Exception {

		// System.out.println(event.getChannel());
		
		switch (event.getChannelEventType()) {
			case ON_MESSAGE_COME:
				process.webSocketProcess(event);
				break;
			case ACTIVE:
				process.as_on_game_client_come(event);
				break;
			case INACTIVE:
				process.as_on_game_client_leave(event);
				break;
			case HTTP_MSG_COME:
				process.httpProcess(event);
				break;
			default:
				throw new Exception("unknown channelEventType <"+event.getChannelEventType()+">");
		}
		
	}
	
}
