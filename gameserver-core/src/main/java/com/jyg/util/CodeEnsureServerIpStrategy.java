package com.jyg.util;

import java.util.HashMap;
import java.util.Map;

import com.jyg.enums.ServerRoleType;

/**
 * created by jiayaoguang at 2018年4月11日
 */
public class CodeEnsureServerIpStrategy implements EnsureServerIpStrategy {
	
	Map<String,ServerRoleType> ipToRoleMap = new HashMap<>();
	

	public ServerRoleType check(String serverIp) {
		
		
		
		return ipToRoleMap.get(serverIp);
	}
	
	
}

