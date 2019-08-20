package com.jyg.timer;

import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2019年8月19日 默认定时器
 */
public class DefaultTimer extends Timer {


	// 回调函数
 	private TimerCallAble timerCallAble;

	public DefaultTimer(int execNum, long firstExecDelayTimeMills, long execIntervalTimeMills , TimerCallAble timerCallAble) {
		super(execNum, firstExecDelayTimeMills, execIntervalTimeMills);
		this.timerCallAble = timerCallAble;
	}

	@Override
	protected void call(){
		timerCallAble.call();
	}

}
