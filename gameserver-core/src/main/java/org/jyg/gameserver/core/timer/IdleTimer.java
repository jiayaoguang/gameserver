package org.jyg.gameserver.core.timer;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2018年4月11日
 */
public class IdleTimer extends Timer{

	public IdleTimer( Channel channel) {
		super(Integer.MAX_VALUE, 5*1000L, 5*1000L);
	}

	@Override
	public void onTime() {

	}

}

