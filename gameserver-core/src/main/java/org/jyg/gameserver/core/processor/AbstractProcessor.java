package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.filter.MsgFilter;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.FTLLoader;
import org.jyg.gameserver.core.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public abstract class AbstractProcessor<T> implements Processor<T> {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

//	private Context context;

	private Consumer consumer;

	private final List<MsgFilter<?>> filters = new ArrayList<>();

	@Override
	public void addMsgFilter(MsgFilter msgFilter){
		filters.add(msgFilter);
	}

	public boolean checkFilters(Session session , EventData eventData){
		if(filters.size() == 0){
			return true;
		}
		for(MsgFilter<?> msgFilter : filters ){
			if(!msgFilter.filter(session , eventData)){
				return false;
			}
		}

		return false;
	}


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

