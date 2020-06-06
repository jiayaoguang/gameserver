package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.util.AsynCallEvent;
import org.jyg.gameserver.core.util.AsynEventAndCallBackRunnable;
import org.jyg.gameserver.core.util.CallBackEvent;
import org.jyg.gameserver.core.consumer.Consumer;

import java.util.concurrent.*;

/**
 * create on 2019/8/15 by jiayaoguang
 */
public class ExecutorManager {

	private final ExecutorService executor;
	private final Consumer defaultConsumer;
	public ExecutorManager(Consumer defaultConsumer) {
		this(10 , defaultConsumer);
	}

	public ExecutorManager(int poolSize, Consumer defaultConsumer) {
		this(poolSize,poolSize , defaultConsumer);
	}

	public ExecutorManager(int corePoolSize, int maxPoolSize, Consumer defaultConsumer) {
		BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1024*16);
		executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 3 * 60 * 1000L, TimeUnit.MILLISECONDS, workQueue, new ThreadPoolExecutor.DiscardPolicy());
		((ThreadPoolExecutor) executor).allowCoreThreadTimeOut(false);
		this.defaultConsumer = defaultConsumer;
	}


	public void execute(Runnable runnable) {
		executor.execute(runnable);
	}

//	public void execute(AsynEventAndCallBackEvent asynEventAndCallBackEvent) {
//		executor.execute(asynEventAndCallBackEvent);
//	}

	/**
	 * @param asynCallEvent 异步Runnable 由线程池线程执行
	 * @param callBackEvent 回调Runnable 由主逻辑线程执行
	 */
	public void execute(AsynCallEvent asynCallEvent, CallBackEvent callBackEvent) {
		executor.execute(new AsynEventAndCallBackRunnable(asynCallEvent , callBackEvent, defaultConsumer));
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
