package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.consumer.Consumer;

/**
 * create on 2020/5/4 by jiayaoguang
 */
public class SingleThreadExecutorManager extends ExecutorManager {

	public SingleThreadExecutorManager(Consumer consumer) {
		super(1, consumer);
	}
}
