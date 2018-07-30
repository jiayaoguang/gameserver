package com.jyg.net;

import com.jyg.handle.initializer.InnerSocketServerInitializer;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class SocketService extends TcpService {

	
	public SocketService(int port) throws Exception {
		super(port ,new InnerSocketServerInitializer());
	}

	
}
