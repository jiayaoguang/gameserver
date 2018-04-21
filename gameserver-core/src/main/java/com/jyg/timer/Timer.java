package com.jyg.timer;

import com.google.protobuf.MessageLiteOrBuilder;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2018年3月15日 定时器
 */
public abstract class Timer implements Comparable<Timer> {

	// 执行次数
	private int exeNum = 0;
	// 开始时间
	private long startTime;
	// 延迟时间
	private final long delayTime;
	
	private Channel channel;
	
	// 回调函数
//	private TimerCallBack callBack;
	
	private long triggerTime;
	
	

	// long triggerTime = startTime + delayTime;

	public long getTriggerTime() {
		return triggerTime;
	}

	public Timer(int exeNum, long delayTime,Channel channel ) {
		super();
		this.exeNum = exeNum;
		this.startTime = System.currentTimeMillis();
		this.delayTime = delayTime;
		this.channel = channel;
	}
	
	public Timer(int exeNum, long startTime, long delayTime,Channel channel ) {
		super();
		this.exeNum = exeNum;
		this.startTime = startTime;
		this.delayTime = delayTime;
		this.channel = channel;
	}

	public int getExeNum() {
		return exeNum;
	}

	public void setExeNum(int exeNum) {
		this.exeNum = exeNum;
	}
	
	public void incExeNum() {
		if(exeNum==0) {
			
			return;
		}
		this.exeNum++;
	}

	public long getStartTime() {
		return startTime;
	}

	void setStartTime(long startTime) {
		this.startTime = startTime;
		this.triggerTime = startTime + this.delayTime;
	}

	public long getDelayTime() {
		return delayTime;
	}

//	public void setDelayTime(long delayTime) {
//		this.delayTime = delayTime;
//	}

//	public TimerCallBack getTimerCallBack() {
//		return callBack;
//	}
//
//	public void setTimerCallBack(TimerCallBack callBack) {
//		this.callBack = callBack;
//	}

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

	public void trigger() {
		this.call();
	}
	
	public abstract void call();
	
	public void writeAndFlush(MessageLiteOrBuilder msg) {
		channel.writeAndFlush(msg);
	}
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	

}
