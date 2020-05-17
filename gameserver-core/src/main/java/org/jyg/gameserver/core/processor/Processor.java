package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.bean.LogicEvent;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public interface Processor<T> extends Cloneable {
	void process(LogicEvent<T> event);
	
}

