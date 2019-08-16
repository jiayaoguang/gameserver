package com.jyg.util;

import java.util.concurrent.*;

/**
 * create on 2019/8/15 by jiayaoguang
 */
public class ExecutorManager {

	private final ExecutorService executor;

	public ExecutorManager() {
		this(10,20);
	}

	public ExecutorManager(int corePoolSize,int maxPoolSize) {
		BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(2048);
		executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 3 * 60 * 1000L, TimeUnit.MILLISECONDS, workQueue, new ThreadPoolExecutor.AbortPolicy());
		((ThreadPoolExecutor) executor).allowCoreThreadTimeOut(false);
	}


	public void execute(Runnable runnable) {
		executor.execute(runnable);
	}

	public Future submit(Runnable runnable) {
		return executor.submit(runnable);
	}

	public <T> Future<T> submit(Callable<T> callable) {
		return executor.submit(callable);
	}

	public void shutdown() {
		executor.shutdown();
	}

	public void shutdownNow() {
		executor.shutdownNow();
	}
}
