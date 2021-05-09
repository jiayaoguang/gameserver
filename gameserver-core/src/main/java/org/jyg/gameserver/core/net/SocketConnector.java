package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.handle.initializer.InnerSocketServerInitializer;
import org.jyg.gameserver.core.util.Context;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class SocketConnector extends TcpConnector {

	
	public SocketConnector(int port , Context context) {
		super(port ,new InnerSocketServerInitializer(context));
	}

	
}
