package org.gameserver.auth.bean;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2018年4月15日
 */
public class UserLoginInfo {

	private Channel channel;
	private String username;
	
	public UserLoginInfo(Channel channel, String username) {
		super();
		this.channel = channel;
		this.username = username;
	}
	
	
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}

