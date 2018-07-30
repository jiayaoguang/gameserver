package com.jyg.net;

import com.jyg.handle.initializer.WebSocketServerInitializer;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class WebSocketService extends TcpService {

	public WebSocketService(int port) throws Exception {
		super(port , new WebSocketServerInitializer());
	}
}
