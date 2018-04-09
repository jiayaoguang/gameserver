package com.jyg.consumers;

import com.jyg.net.EventDispatcher;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class EventConsumerFactory {

	public static EventConsumer newEventConsumer() {
		return new EventConsumer();
	}

	
	
}
