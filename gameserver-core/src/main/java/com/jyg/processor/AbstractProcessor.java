package com.jyg.processor;

import com.jyg.bean.LogicEvent;
import com.jyg.consumer.EventConsumer;
import com.jyg.util.FTLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public abstract class AbstractProcessor<T> implements Processor<T> {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final FTLLoader ftlLoader = new FTLLoader();

	private EventConsumer eventConsumer;

	public EventConsumer getEventConsumer() {
		return eventConsumer;
	}

	public void setEventConsumer(EventConsumer eventConsumer){
		this.eventConsumer = eventConsumer;
	}

	public abstract void process(LogicEvent<T> event);
	
}

