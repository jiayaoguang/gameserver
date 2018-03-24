package com.jyg.timer;
/**
 * created by jiayaoguang at 2018年3月22日
 */

import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class TimerTrigger {

	PriorityQueue<Timer> queue = new PriorityQueue<>();

	public void addTimer(Timer timer) {
		timer.setStartTime(System.currentTimeMillis());
		queue.add(timer);
	}
	
	public void tick() {
		
		
		
	}
}
