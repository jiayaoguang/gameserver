package com.jyg.timer;

import com.google.protobuf.MessageLiteOrBuilder;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by jiayaoguang at 2018年3月15日 定时器
 */
public abstract class Timer {

	private static final Logger LOGGER = LoggerFactory.getLogger(Timer.class);

	// 执行次数
	private int execNum = 0;
	// 开始时间
	private final long startTime;
	// 第一次执行的延迟时间
	private final long firstExecDelayTimeMills;
	//执行的间隔时间
	private long execIntervalTimeMills;

	private Channel channel;

	// 回调函数
//	private TimerCallBack callBack;
	//执行时间戳(毫秒)
	private long triggerTime;

	private boolean isCancel = false;


	public Timer(int execNum, long firstExecDelayTimeMills, long execIntervalTimeMills) {
		super();
		this.execNum = execNum;
		this.execIntervalTimeMills = execIntervalTimeMills;
		this.firstExecDelayTimeMills = firstExecDelayTimeMills;

		this.startTime = System.currentTimeMillis();
		triggerTime = startTime + firstExecDelayTimeMills;
	}


	public long getTriggerTime() {
		return triggerTime;
	}

	public int getExecNum() {
		return execNum;
	}

	public void setExecNum(int execNum) {
		this.execNum = execNum;
	}

	void reduceExecNum(int reduceNum) {
		this.execNum -= reduceNum;
	}

	private void setNextTriggerTime() {
		this.triggerTime = System.currentTimeMillis() + this.execIntervalTimeMills;
	}

	public long getFirstExecDelayTimeMills() {
		return firstExecDelayTimeMills;
	}

	public void cancel() {
		isCancel = true;
	}

	public boolean isCancel() {
		return isCancel;
	}

	public boolean isEnd() {
		return execNum <= 0;
	}


	final void trigger() {

		setNextTriggerTime();
		reduceExecNum(1);

		try {
			this.call();
		} catch (Exception e) {
			LOGGER.error(" timer call make execption {} ", e);
		}
	}


	protected abstract void call();

}
