package com.jyg.consumers;

import com.jyg.bean.LogicEvent;
import com.jyg.net.EventDispatcher;
import com.jyg.net.Request;
import com.jyg.timer.DelayCloseTimer;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class EventConsumer implements EventHandler<LogicEvent>, WorkHandler<LogicEvent> {


	private final EventDispatcher dispatcher = EventDispatcher.getInstance();
	

	public EventConsumer() {

	}

	@Override
	public void onEvent(LogicEvent event, long sequence, boolean endOfBatch) {
		
		try {
			this.onEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private int eventTimes = 0;
	
	private long requestId = 1;
	
	@Override
	public void onEvent(LogicEvent event) throws Exception {

		// System.out.println(event.getChannel());
		try {
			switch (event.getChannelEventType()) {
				
				case CLIENT_SOCKET_CONNECT_ACTIVE:
					dispatcher.as_on_client_active(event);
					break;
				case CLIENT_SOCKET_CONNECT_INACTIVE:
					dispatcher.as_on_client_inactive(event);
					break;
					
				case INNER_SOCKET_CONNECT_ACTIVE:
					dispatcher.as_on_inner_server_active(event);
					break;
				case INNER_SOCKET_CONNECT_INACTIVE:
					dispatcher.as_on_inner_server_inactive(event);
					break;
					
				case HTTP_MSG_COME:
					((Request)event.getData()).setRequestid(requestId++);;
					dispatcher.httpProcess(event);
					// 5秒后关闭
					EventDispatcher.getInstance().addTimer(new DelayCloseTimer(event.getChannel(), 5));
					break;
				case ON_MESSAGE_COME:
					dispatcher.webSocketProcess(event);
					break;
				case RPC_MSG_COME:
					dispatcher.socketProcess(event);
					break;
					
				case ON_TEXT_MESSAGE_COME:
					System.out.println(event.getData());
					break;
				default:
					throw new Exception("unknown channelEventType <"+event.getChannelEventType()+">");
			}
		}finally {
			eventTimes++;
			if (eventTimes == 10000) {
				eventTimes = 0;
				dispatcher.loop();
			}
		}
		
		
	}
	
}
