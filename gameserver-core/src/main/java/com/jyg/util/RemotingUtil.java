package com.jyg.util;

import io.netty.channel.epoll.Epoll;

/**
 * created by jiayaoguang at 2018年4月21日
 */
public class RemotingUtil {

	public static final String OS_NAME = System.getProperty("os.name");
	
	
	public static final boolean isLinuxPlatform;
	
	static{
		if(OS_NAME!=null&& OS_NAME.toLowerCase().contains("linux")) {
			isLinuxPlatform = true;
		}else {
			isLinuxPlatform = false;
		}
	}
	
	//TODO
	public static boolean useEpoll() {
		return RemotingUtil.isLinuxPlatform&&Epoll.isAvailable();
	}
	
}

