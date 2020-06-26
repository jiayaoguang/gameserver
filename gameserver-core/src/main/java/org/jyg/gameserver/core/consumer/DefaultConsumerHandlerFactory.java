package org.jyg.gameserver.core.consumer;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class DefaultConsumerHandlerFactory extends ConsumerHandlerFactory {

	public ConsumerHandler newEventConsumer() {
		return new ConsumerHandler();
	}

}
