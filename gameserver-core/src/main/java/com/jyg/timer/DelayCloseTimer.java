package com.jyg.timer;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2018年4月15日
 */
public class DelayCloseTimer extends Timer{

	private Channel channel;

	public DelayCloseTimer(Channel channel){
		super(1 , 5*1000L, 5*1000L);
		this.channel = channel;
	}
	
	public DelayCloseTimer(Channel channel,long delayTimeMills){
		super(1 , delayTimeMills, delayTimeMills);
		this.channel = channel;
	}
	
	public void onTime() {
		//open -> register ->active
		if(channel.isOpen()){
			System.out.println("out of time,just close it");
			channel.close();
		}
	}
	
}

