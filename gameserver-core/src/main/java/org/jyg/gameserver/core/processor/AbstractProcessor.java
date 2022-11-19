package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.intercept.MsgInterceptor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.GameContext;
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

	private GameConsumer gameConsumer;

	private final List<MsgInterceptor<?>> interceptors = new ArrayList<>();

	@Override
	public void addMsgInterceptor(MsgInterceptor msgInterceptor){
		interceptors.add(msgInterceptor);
	}

	public boolean checkIntercepts(Session session , EventData eventData){
		if(interceptors.size() == 0){
			return true;
		}
		for(MsgInterceptor<?> msgInterceptor : interceptors){
			if(!msgInterceptor.intercept(session , eventData)){
				return false;
			}
		}

		return true;
	}


	public GameContext getContext() {
		return gameConsumer.getGameContext();
	}


	public abstract void process(Session session , EventData<T> event);


	public GameConsumer getGameConsumer() {
		return gameConsumer;
	}

	public void setGameConsumer(GameConsumer gameConsumer) {
		this.gameConsumer = gameConsumer;
	}


}

