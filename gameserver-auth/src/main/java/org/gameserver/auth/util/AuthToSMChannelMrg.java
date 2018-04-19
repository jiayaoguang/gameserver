package org.gameserver.auth.util;

import com.google.inject.Inject;
import com.jyg.net.EventDispatcher;
import com.jyg.startup.InnerClient;
import com.jyg.timer.FreeChannelDelayCloseTimer;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2018年4月9日
 */
public class AuthToSMChannelMrg {

	//如果同时登陆玩家会过多，则可以增加连接
	private Channel channel;
	
	private InnerClient client = new InnerClient();
	
	private FreeChannelDelayCloseTimer delayCloseChannelTimer;
	
	@Inject
	public AuthToSMChannelMrg() {
		checkoutAndGetChannel();
		System.out.println("get channel <" + channel);
	}
	

	public Channel checkoutAndGetChannel() {
		if(channel != null && channel.isOpen()) {
			delayCloseChannelTimer.setRecentUseTime(System.currentTimeMillis());
			return channel;
		}
		
		delayCloseChannelTimer = new FreeChannelDelayCloseTimer(channel);
		
		try {
			channel = client.connect("localhost", 9001);
			delayCloseChannelTimer.setChannel(channel);
			EventDispatcher.getInstance().addTimer(delayCloseChannelTimer);
			System.out.println("just create new channel <" + channel);
			return channel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("获取 SM 连接超时");
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
//		
//		return checkoutAndGetChannel();
		
		return null;
	}
	

}

