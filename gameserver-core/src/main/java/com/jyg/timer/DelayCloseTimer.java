package com.jyg.timer;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2018年4月15日
 */
public class DelayCloseTimer extends Timer{
	public DelayCloseTimer(Channel channel){
		super(1 , 5*1000L, channel);
	}
	
	public DelayCloseTimer(Channel channel,int delaySeconds){
		super(1 , delaySeconds*1000L, channel);
	}
	
	public void call() {
		//open -> register ->active
		if(this.getChannel().isOpen()){
			System.out.println("out of time,just close it");
			this.getChannel().close();
		}
	}
	
}

