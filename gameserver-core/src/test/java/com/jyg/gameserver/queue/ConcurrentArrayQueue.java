package com.jyg.gameserver.queue;
/**
 * created by jiayaoguang at 2018年5月26日
 */

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

public class ConcurrentArrayQueue {

	private final LongAdder blockQueueNum = new LongAdder();

	private int queueSize;
	List<FixArrayQueue> queues = new ArrayList<>();

	private final ThreadLocal<FixArrayQueue> localQueue = new ThreadLocal();

	public ConcurrentArrayQueue(int queueSize) {
		this.queueSize = queueSize;
	}

	public boolean offer(Object content) {
		FixArrayQueue queue = localQueue.get();
		if (queue == null) {
			queue = new FixArrayQueue();
			localQueue.set(queue);
			synchronized (queues) {
				queues.add(queue);
			}
		}
		if (!queue.offer(content)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	public Object poll() {
		FixArrayQueue recentQueue = queues.get(0);
		Data recentData = null;

		for (FixArrayQueue queue : queues) {
			Data data = queue.peekData();
			if (data != null) {
				if(recentData == null) {
					recentData = data;
					recentQueue = queue;
				}else if(recentData.getTimeMills() > data.getTimeMills()) {
					recentData = data;
					recentQueue = queue;
				}
			}
		}
		
		return recentQueue.poll();
	}

}
