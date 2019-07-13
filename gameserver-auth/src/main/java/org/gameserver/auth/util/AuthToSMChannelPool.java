package org.gameserver.auth.util;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.gameserver.auth.bean.Tuple;

import com.google.inject.Inject;
import com.jyg.net.EventDispatcher;
import com.jyg.startup.TcpClient;
import com.jyg.timer.FreeChannelDelayCloseTimer;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2018年4月9日
 */
@Deprecated
public class AuthToSMChannelPool {

	//如果同时登陆玩家会过多，则可以增加连接
	private ArrayBlockingQueue<Tuple<Channel,FreeChannelDelayCloseTimer>> smChannelQueue = new ArrayBlockingQueue<>(1000);
	
	private TcpClient client = new TcpClient();
	
	AtomicInteger channelNum = new AtomicInteger(0);
	
	@Inject
	public AuthToSMChannelPool() {
		checkoutAndGetChannel();
		System.out.println("just get  center channel " + smChannelQueue.peek());
	}
	

	public Tuple< Channel, FreeChannelDelayCloseTimer > checkoutAndGetChannel() {
		while(!smChannelQueue.isEmpty()) {
			Tuple<Channel, FreeChannelDelayCloseTimer> channelAndTimer = smChannelQueue.poll();
			if(channelAndTimer != null && channelAndTimer.x.isOpen()) {
				channelAndTimer.y.setRecentUseTime(System.currentTimeMillis());
				return channelAndTimer;
			}
		}
		
		
		try {
			Channel channel = client.connect("localhost", 9001);
			FreeChannelDelayCloseTimer delayCloseChannelTimer = new FreeChannelDelayCloseTimer(channel);
			EventDispatcher.getInstance().addTimer(delayCloseChannelTimer);
			System.out.println("just create new channel <" + channel);
			return new Tuple<Channel, FreeChannelDelayCloseTimer>( channel,delayCloseChannelTimer );
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("获取 SM 连接超时");
		
		return null;
	}
	
	
	
	public void returnChannel(Tuple<Channel, FreeChannelDelayCloseTimer> channelAndTimer) {
		
		if(channelAndTimer == null) {
			return;
		}
		
		smChannelQueue.add(channelAndTimer);
//		channelNum.incrementAndGet();
		InetSocketAddress inet = (InetSocketAddress)channelAndTimer.x.remoteAddress();
		String ip = inet.getHostName()+inet.getPort();
	}
	

}

