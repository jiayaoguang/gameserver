package com.jyg.net;

import com.jyg.handle.RpcServerInitializer;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class RpcService extends Service {

	
	public RpcService(int port) throws Exception {
		super(port ,new RpcServerInitializer());
	}

	
}
