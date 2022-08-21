package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.consumer.GameConsumer;

/**
 * create on 2020/5/4 by jiayaoguang
 */
public class SingleThreadExecutorManager extends ExecutorManager {

	public SingleThreadExecutorManager(GameConsumer gameConsumer) {
		super(1, gameConsumer);
	}
}
