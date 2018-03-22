package com.jyg.net;

import com.jyg.handle.HttpServerInitializer;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class HttpService extends Service {

	/**
	 * 默认处理http是线程同步的
	 * @param port
	 * @throws Exception
	 */
	
	public HttpService(int port) throws Exception {
		super(port , new HttpServerInitializer());
	}

	/**
	 * @param port
	 * @param isSynHttp 处理http是否线程同步的
	 * @throws Exception
	 */
	public HttpService(int port,boolean isSynHttp) throws Exception {
		super(port , new HttpServerInitializer(isSynHttp));
	}
	
}
