package org.gameserver.auth.timer;

import com.jyg.proto.p_common.p_common_request_ping;
import com.jyg.timer.Timer;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2018年4月11日
 */
public class IdleTimer extends Timer{

	public IdleTimer( Channel channel) {
		super(Integer.MAX_VALUE, 5*1000, channel);
	}

	@Override
	public void call() {
		
		this.getChannel().writeAndFlush(p_common_request_ping.newBuilder());
		
	}

}

