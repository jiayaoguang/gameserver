package com.jyg.consumer;

import com.jyg.net.EventDispatcher;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public abstract class EventConsumerFactory {

	private EventDispatcher eventDispatcher;

	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	public void setEventDispatcher(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	public final EventConsumer createAndInit(){
		EventConsumer eventConsumer = newEventConsumer();
		eventConsumer.setDispatcher(eventDispatcher);
		eventConsumer.init();
		return eventConsumer;
	}

	public abstract EventConsumer newEventConsumer();

}
