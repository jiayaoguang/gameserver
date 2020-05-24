package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.FTLLoader;
import org.jyg.gameserver.core.consumer.IGlobalQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public abstract class AbstractProcessor<T> implements Processor<T> {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected static final FTLLoader ftlLoader = new FTLLoader();

	private Context context;

	private int consumerId = 0;

	public IGlobalQueue getGlobalQueue() {
		return context.getGlobalQueue();
	}


	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public int getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(int consumerId) {
		this.consumerId = consumerId;
	}

	public abstract void process(Session session , LogicEvent<T> event);


	
}

