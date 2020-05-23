package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.util.Context;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public abstract class EventConsumerFactory {


	public final EventConsumer createAndInit(Context context){
		EventConsumer eventConsumer = newEventConsumer();
		eventConsumer.setContext(context);
		eventConsumer.init();
		return eventConsumer;
	}

	protected abstract EventConsumer newEventConsumer();

}
