package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.FTLLoader;
import org.jyg.gameserver.core.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public abstract class AbstractProcessor<T> implements Processor<T> {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

//	private Context context;

	private Consumer consumer;



	public Context getContext() {
		return consumer.getContext();
	}


	public abstract void process(Session session , EventData<T> event);


	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

}

