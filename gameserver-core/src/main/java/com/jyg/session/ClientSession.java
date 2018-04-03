package com.jyg.session;

import com.jyg.bean.GameObject;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class ClientSession extends Session{

	private GameObject gameObject;
	
	private String token;
	
	public ClientSession(Channel channel, long sessionId) {
		super(channel, sessionId);
	}

	
}

