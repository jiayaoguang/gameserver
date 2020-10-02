package org.jyg.gameserver.core.util;

import io.netty.channel.epoll.Epoll;

/**
 * created by jiayaoguang at 2018年4月21日
 */
public class RemotingUtil {


	public static final boolean IS_LINUX_PLATFORM;

	private RemotingUtil() {
		
	}
	static {
		final String OS_NAME = System.getProperty("os.name");
		if (OS_NAME != null && OS_NAME.toLowerCase().contains("linux")) {
			IS_LINUX_PLATFORM = true;
		} else {
			IS_LINUX_PLATFORM = false;
		}
	}

	// TODO
	public static boolean isLinuxPlatform() {
		return RemotingUtil.IS_LINUX_PLATFORM;
	}

}
