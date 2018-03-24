package com.jyg.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public class Constants {

	//websocket端口
	public static final int SERVER_WEBSOCKET_PORT;
	//http端口
	public static final int SERVER_PORT;
	
	
	static {
		Properties pro = new Properties();
		
		try {
			pro.load(new FileInputStream("jyg.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SERVER_PORT = Integer.parseInt(pro.getProperty("server.port"));
		
		SERVER_WEBSOCKET_PORT = Integer.parseInt(pro.getProperty("server.websocket.port"));
	}
	
	
}

