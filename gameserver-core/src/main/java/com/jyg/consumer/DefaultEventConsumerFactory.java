package com.jyg.consumer;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class DefaultEventConsumerFactory extends EventConsumerFactory {

	public EventConsumer newEventConsumer() {
		return new DefaultEventConsumer();
	}

}
