package com.jyg.timer;

/**
 * created by jiayaoguang at 2019年8月19日 默认定时器
 */
public class DefaultTimer extends Timer {


	// 回调函数
 	private ITimerHandler timerCallAble;

	public DefaultTimer(int execNum, long firstExecDelayTimeMills, long execIntervalTimeMills , ITimerHandler timerCallAble) {
		super(execNum, firstExecDelayTimeMills, execIntervalTimeMills);
		this.timerCallAble = timerCallAble;
	}

	@Override
	protected void call(){
		timerCallAble.call();
	}

}
