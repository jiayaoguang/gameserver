package com.jyg.timer;


import io.netty.channel.Channel;
/**
 * created by jiayaoguang at 2018年4月10日
 * 闲置连接延迟关闭
 */
public class FreeChannelDelayCloseTimer extends Timer {

	long recentUseTime=System.currentTimeMillis();
	
	public FreeChannelDelayCloseTimer( Channel channel) {
		
		super(1, 10*1000, channel);
		
	}
	
	public void setRecentUseTime(long recentUseTime) {
		this.recentUseTime = recentUseTime;
	}
	
	
	public void call() {
		if(!this.getChannel().isOpen()) {
			return;
		}
		if(recentUseTime+this.getDelayTime() <= this.getTriggerTime() ) {
			this.getChannel().close();
			
		}else {
			this.setStartTime(System.currentTimeMillis());
			this.setExeNum( getExeNum()+1);
		}
		
	}
	
}

