package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.intercept.MsgInterceptor;
import org.jyg.gameserver.core.session.Session;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public interface Processor<T> extends Cloneable {
	void process(Session session , MsgEvent<T> event);

	public void setMsgInterceptor(MsgInterceptor msgInterceptor);

	public boolean checkIntercepts(Session session , MsgEvent eventData);
	
}

