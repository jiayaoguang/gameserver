package com.jyg.consumer;

import com.jyg.bean.LogicEvent;
import com.jyg.net.EventDispatcher;
import com.jyg.net.Request;
import com.jyg.timer.DelayCloseTimer;
import com.jyg.timer.TimerManager;
import com.jyg.util.CallBackEvent;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public abstract class EventConsumer implements EventHandler<LogicEvent>, WorkHandler<LogicEvent> {


	private final TimerManager timerManager = new TimerManager();

	private EventDispatcher dispatcher;

	private int requestId = 1;

	public EventConsumer() {

	}


	@Override
	public final void onEvent(LogicEvent event, long sequence, boolean endOfBatch) {

		try {
			this.onEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	public final void onEvent(LogicEvent event) throws Exception {

		// System.out.println(event.getChannel());
		try {
			doEvent(event);
		} finally {
			dispatcher.loop();
		}

	}

	private void doEvent(LogicEvent event) {
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
				((Request) event.getData()).setRequestid(getAndIncRequestId());
				dispatcher.httpProcess(event);
				// 5秒后关闭
				timerManager.addTimer(new DelayCloseTimer(event.getChannel(), 5 * 1000L));
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

			case INNER_MSG:
				doInnerMsg(event.getData());
				break;

			default:
				throw new IllegalArgumentException("unknown channelEventType <" + event.getChannelEventType() + ">");
		}
	}

	private void doInnerMsg(Object msg){
		if(!(msg instanceof CallBackEvent)){
			return;
		}
		CallBackEvent callBackEvent = (CallBackEvent) msg;
		callBackEvent.execte(callBackEvent.getData());
	}


	private int getAndIncRequestId() {
		if (requestId == Integer.MAX_VALUE) {
			requestId = 0;
		}
		return requestId++;
	}


	public EventDispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(EventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	protected void init(){
		dispatcher.init(timerManager);
	}

	/**
	 *
	 */
	protected abstract void loop();
}
