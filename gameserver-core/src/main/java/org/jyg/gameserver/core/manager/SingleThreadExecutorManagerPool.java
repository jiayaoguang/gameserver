package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.session.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * create on 2020/5/4 by jiayaoguang
 */
public class SingleThreadExecutorManagerPool {

	public static final int DEFAULT_POOL_SIZE = 4;

	private final List<SingleThreadExecutorManager> singleThreadExecutorManagerList;

	private final GameConsumer defaultGameConsumer;

	public SingleThreadExecutorManagerPool(GameConsumer defaultGameConsumer) {
		this(defaultGameConsumer, DEFAULT_POOL_SIZE);
	}

	public SingleThreadExecutorManagerPool(GameConsumer defaultGameConsumer, int num) {
		this.singleThreadExecutorManagerList = new ArrayList<>();
		for(int i = 0;i < num ;i++){
			this.singleThreadExecutorManagerList.add(new SingleThreadExecutorManager(defaultGameConsumer));
		}
		this.defaultGameConsumer = defaultGameConsumer;
	}


	public SingleThreadExecutorManager getSingleThreadExecutorManager(long playerUid){
		int executorIndex = (int)(playerUid % singleThreadExecutorManagerList.size());
		return singleThreadExecutorManagerList.get(executorIndex);
	}

	@Deprecated
	public void expandSize(int addNum) {
		for (int i = 0; i < addNum; i++) {
			singleThreadExecutorManagerList.add(new SingleThreadExecutorManager(this.defaultGameConsumer));
		}
	}

}
