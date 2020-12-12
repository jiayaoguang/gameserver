package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.handle.initializer.HttpServerInitializer;
import org.jyg.gameserver.core.util.Context;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class HttpService extends TcpService {

	/**
	 * @param port 端口
	 * @param context context
	 */
	public HttpService(int port , Context context) {
		super(port , new HttpServerInitializer(context) , true);
	}

//	public void start(){
//
//	}

	
}
