package com.jyg.timer;
/**
 * created by jiayaoguang at 2018年3月15日
 * 定时器
 */
public class Timer {
	
	//执行次数
	private int exeNum = 0;
	//开始时间
	private long startTime; 
	//延迟时间
	private long delayTime;
	//回调函数
	TimerCallBack timerCallBack;
	
	
	public Timer(int exeNum, long startTime, long delayTime, TimerCallBack timerCallBack) {
		super();
		this.exeNum = exeNum;
		this.startTime = startTime;
		this.delayTime = delayTime;
		this.timerCallBack = timerCallBack;
	}
	
	public int getExeNum() {
		return exeNum;
	}
	public void setExeNum(int exeNum) {
		this.exeNum = exeNum;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getDelayTime() {
		return delayTime;
	}
	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}
	public TimerCallBack getTimerCallBack() {
		return timerCallBack;
	}
	public void setTimerCallBack(TimerCallBack timerCallBack) {
		this.timerCallBack = timerCallBack;
	}
	
	
	

}

