package com.jyg.net;

import com.jyg.handle.initializer.HttpServerInitializer;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class HttpService extends TcpService {

	/**
	 * @param port
	 * @throws Exception
	 */
	
	public HttpService(int port) throws Exception {
		super(port , new HttpServerInitializer());
	}

	
}
