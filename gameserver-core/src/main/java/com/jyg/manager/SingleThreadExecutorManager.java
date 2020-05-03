package com.jyg.manager;

import com.jyg.util.IGlobalQueue;

/**
 * create on 2019/8/15 by jiayaoguang
 */
public class SingleThreadExecutorManager extends ExecutorManager {

	public SingleThreadExecutorManager(IGlobalQueue globalQueue) {
		super(1,1 , globalQueue);
	}
}
