package com.jyg.util;

import com.jyg.enums.ServerRoleType;

/**
 * created by jiayaoguang at 2018年4月11日
 */
public interface EnsureServerIpStrategy {

	ServerRoleType check(String serverIp);
	
}

