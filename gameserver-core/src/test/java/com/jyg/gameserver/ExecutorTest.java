package com.jyg.gameserver;

import com.jyg.util.ExecutorManager;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * create on 2019/8/16 by jiayaoguang
 */
public class ExecutorTest {


	@Test
	public void testSubmit() {
		ExecutorManager executorManager = new ExecutorManager();
		Future future = executorManager.submit(new Runnable() {
			@Override
			public void run() {

				try {
					Thread.sleep(10 * 1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		});
		System.out.println(" ..... " + future.isDone());


	}

}
