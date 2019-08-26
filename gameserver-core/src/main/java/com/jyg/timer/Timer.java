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
//	private final long firstDelayTimeMills;
	//执行的间隔时间
	private long delayTimeMills;

	private Channel channel;

	// 回调函数
//	private TimerCallBack callBack;
	//执行时间戳(毫秒)
	private long triggerTime;

	private boolean isCancel = false;


	public Timer(int execNum, long firstDelayTimeMills, long delayTimeMills) {
		this.execNum = execNum;
		this.delayTimeMills = delayTimeMills;
//		this.firstDelayTimeMills = firstDelayTimeMills;

		this.startTime = System.currentTimeMillis();
		triggerTime = startTime + firstDelayTimeMills;
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

	private void updateNextTriggerTime() {
		this.triggerTime = System.currentTimeMillis() + this.delayTimeMills;
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

		updateNextTriggerTime();
		reduceExecNum(1);

		try {
			this.call();
		} catch (Exception e) {
			LOGGER.error(" timer call make execption {} ", e);
		}
	}


	protected abstract void call();

}
