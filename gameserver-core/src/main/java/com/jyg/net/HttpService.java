package com.jyg.net;

import com.jyg.handle.initializer.HttpServerInitializer;
import com.jyg.util.Context;
import com.jyg.util.IGlobalQueue;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class HttpService extends TcpService {

	/**
	 * @param port
	 * @throws Exception
	 */
	
	public HttpService(int port , Context context) {
		super(port , new HttpServerInitializer(context) , true);
	}

//	public void start(){
//
//	}

	
}
