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
public abstract class EventConsumer implements EventHandler<LogicEvent>, WorkHandler<LogicEvent> {


	private final EventDispatcher dispatcher = EventDispatcher.getInstance();


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

	private int requestId = 1;

	@Override
	public final void onEvent(LogicEvent event) throws Exception {

		// System.out.println(event.getChannel());
		try {
			dealEvent(event);
		} finally {
			dispatcher.loop();
		}

	}

	private void dealEvent(LogicEvent event) {
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
				throw new IllegalArgumentException("unknown channelEventType <" + event.getChannelEventType() + ">");
		}
	}

	private int getAndIncRequestId() {
		if (requestId == Integer.MAX_VALUE) {
			requestId = 0;
		}
		return requestId++;
	}

	/**
	 *
	 */
	protected abstract void loop() ;
}
