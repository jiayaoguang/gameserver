package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.util.Context;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public abstract class ConsumerHandlerFactory {


	public final ConsumerHandler createAndInit(Context context){
		ConsumerHandler consumerHandler = newEventConsumer();
		consumerHandler.setContext(context);
		consumerHandler.init();
		return consumerHandler;
	}

	protected abstract ConsumerHandler newEventConsumer();

}
