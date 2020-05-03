package com.jyg.processor;

import com.jyg.bean.LogicEvent;
import com.jyg.consumer.EventConsumer;
import com.jyg.net.EventDispatcher;
import com.jyg.util.FTLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public abstract class AbstractProcessor<T> implements Processor<T> {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected static final FTLLoader ftlLoader = new FTLLoader();

	private EventDispatcher eventDispatcher;

	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	public void setEventDispatcher(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	public abstract void process(LogicEvent<T> event);
	
}

