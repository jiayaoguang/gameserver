package com.jyg.net;

import com.jyg.handle.initializer.InnerSocketServerInitializer;
import com.jyg.util.Context;
import com.jyg.util.IGlobalQueue;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class SocketService extends TcpService {

	
	public SocketService(int port , Context context) {
		super(port ,new InnerSocketServerInitializer(context));
	}

	
}
