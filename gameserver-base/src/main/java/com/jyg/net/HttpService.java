package com.jyg.net;

import com.jyg.handle.HttpServerInitializer;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class HttpService extends Service {

	
	public HttpService(int port) {
		super(port , new HttpServerInitializer());
	}

	
}
