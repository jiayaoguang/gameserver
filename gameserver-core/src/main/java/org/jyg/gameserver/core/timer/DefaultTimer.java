package org.jyg.gameserver.core.timer;

/**
 * created by jiayaoguang at 2019年8月19日 默认定时器
 */
public class DefaultTimer extends Timer {

	// 回调函数
 	private final ITimerHandler timerHandler;


	public DefaultTimer(long execDelayTimeMills , ITimerHandler timerHandler) {
		this(1, execDelayTimeMills,timerHandler);
	}

	public DefaultTimer(int execNum ,long execIntervalTimeMills , ITimerHandler timerHandler) {
		this(execNum, System.currentTimeMillis() + execIntervalTimeMills, execIntervalTimeMills,timerHandler);
	}

	public DefaultTimer(int execNum, long firstExecTimeMills, long execIntervalTimeMills , ITimerHandler timerHandler) {
		super(execNum, firstExecTimeMills, execIntervalTimeMills);
		this.timerHandler = timerHandler;
	}



	@Override
	protected void onTime(){
		if(timerHandler != null){
			timerHandler.onTime();
		}
	}

	@Override
	public String toString(){
		return "DefaultTimer."+timerHandler.getClass().getName();
	}




}
