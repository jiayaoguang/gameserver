package com.jyg.session;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class ServerSession extends Session{

	public ServerSession(Channel channel,long sessionId) {
		super(channel, sessionId);
	}

	
	
	
}

