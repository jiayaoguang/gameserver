package org.jyg.gameserver.core.timer;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by jiayaoguang at 2018年3月15日 定时器
 */
public abstract class Timer implements Comparable<Timer>{

	private static final Logger LOGGER = LoggerFactory.getLogger(Timer.class);

	// 执行次数
	private int execNum = 0;
	// 开始时间
//	private final long startTime;
	// 第一次执行的延迟时间
//	private final long firstDelayTimeMills;
	//执行的间隔时间
	private long intervalTimeMills;


	private Channel channel;

	// 回调函数
//	private TimerCallBack callBack;
	//执行时间戳(毫秒)
	private long triggerTime;

	private boolean isCancel = false;


	private boolean ignoreExecTime = false;


	public Timer(int execNum, long intervalTimeMills) {
		this(execNum , System.currentTimeMillis() + intervalTimeMills , intervalTimeMills);
	}


	public Timer(int execNum, long firstExecTime, long intervalTimeMills) {
		this.execNum = execNum;
		this.intervalTimeMills = intervalTimeMills;
//		this.firstDelayTimeMills = firstDelayTimeMills;

//		this.startTime = startTime;

		this.triggerTime = firstExecTime;


	}


	public long getTriggerTime() {
		return triggerTime;
	}

	public long getIntervalTimeMills() {
		return intervalTimeMills;
	}

	public int getExecNum() {
		return execNum;
	}

	void setExecNum(int execNum) {
		this.execNum = execNum;
	}

	void reduceExecNum(int reduceNum) {
		this.execNum -= reduceNum;
	}

	private void updateNextTriggerTime() {
		this.triggerTime = System.currentTimeMillis() + this.intervalTimeMills;
	}

	void setTriggerTime(long triggerTime) {
		this.triggerTime = triggerTime;
	}

	public void cancel() {
		isCancel = true;
	}

	public boolean isCancel() {
		return isCancel;
	}

	public boolean isIgnoreExecTime() {
		return ignoreExecTime;
	}

	public void setIgnoreExecTime(boolean ignoreExecTime) {
		this.ignoreExecTime = ignoreExecTime;
	}


	public boolean isEnd() {
		if(execNum == -1){
			return false;
		}
		return execNum <= 0;
	}


//	final void trigger() {
//
//		updateNextTriggerTime();
//		if(execNum != -1){
//			reduceExecNum(1);
//		}
//
//		try {
//			this.onTime();
//		} catch (Exception e) {
//			LOGGER.error(" timer onTime make execption {} ", ExceptionUtils.getStackTrace(e));
//		}
//	}


	protected abstract void onTime();

	@Override
	public int compareTo(Timer timer2) {
		long triggerTime1 = this.getTriggerTime();
		long triggerTime2 = timer2.getTriggerTime();
		if (triggerTime1 > triggerTime2) {
			return 1;
		} else if (triggerTime1 == triggerTime2) {
			return 0;
		} else {
			return -1;
		}
	}

}
