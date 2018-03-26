package com.jyg.net;

import com.jyg.handle.SocketServerInitializer;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class SocketService extends Service {

	
	public SocketService(int port) throws Exception {
		super(port ,new SocketServerInitializer());
	}

	
}
