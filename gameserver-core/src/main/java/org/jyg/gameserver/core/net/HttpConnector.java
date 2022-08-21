package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.handle.initializer.HttpServerInitializer;
import org.jyg.gameserver.core.util.GameContext;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class HttpConnector extends TcpConnector {

	/**
	 * @param port 端口
	 * @param gameContext context
	 */
	public HttpConnector(int port , GameContext gameContext) {
		super(port , new HttpServerInitializer(gameContext) , true);
	}

//	public void start(){
//
//	}

	
}
