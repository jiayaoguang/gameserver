package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.consumer.Consumer;

import java.util.ArrayList;
import java.util.List;

/**
 * create on 2020/5/4 by jiayaoguang
 */
public class SingleThreadExecutorManagerPool {

	public static final int DEFAULT_POOL_SIZE = 4;

	private final List<SingleThreadExecutorManager> singleThreadExecutorManagerList;

	private final Consumer globalQueue;

	public SingleThreadExecutorManagerPool(Consumer globalQueue ) {
		this(globalQueue , DEFAULT_POOL_SIZE);
	}

	public SingleThreadExecutorManagerPool(Consumer globalQueue , int num) {
		this.singleThreadExecutorManagerList = new ArrayList<>();
		for(int i = 0;i < num ;i++){
			this.singleThreadExecutorManagerList.add(new SingleThreadExecutorManager(globalQueue));
		}
		this.globalQueue = globalQueue;
	}

	public SingleThreadExecutorManager getSingleThreadExecutorManager(Session session){
		return getSingleThreadExecutorManager(session.getSessionId());
	}

	public SingleThreadExecutorManager getSingleThreadExecutorManager(int num){
		int executorIndex = num % singleThreadExecutorManagerList.size();
		return singleThreadExecutorManagerList.get(executorIndex);
	}

	@Deprecated
	public void expandSize(int addNum) {
		for (int i = 0; i < addNum; i++) {
			singleThreadExecutorManagerList.add(new SingleThreadExecutorManager(this.globalQueue));
		}
	}

}
