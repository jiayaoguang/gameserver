package com.jyg.session;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class HttpSession extends Session{

	public HttpSession(Channel channel,int sessionId) {
		super(channel, sessionId);
	}
	
	
	
}

