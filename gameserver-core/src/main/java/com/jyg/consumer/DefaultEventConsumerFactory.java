package com.jyg.consumer;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class DefaultEventConsumerFactory implements EventConsumerFactory {

	public EventConsumer newEventConsumer() {
		return new DefaultEventConsumer();
	}

}
