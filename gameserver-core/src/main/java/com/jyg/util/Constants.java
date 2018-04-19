package com.jyg.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public class Constants {

	//websocket端口
//	public static final int SERVER_WEBSOCKET_PORT;
//	//http端口
//	public static final int SERVER_PORT;
	
	public static final String HTTP_ROOT_DIR; 
	
	//public static final int SOCKET_SO_LINGER;
	static {
		Properties pro = new Properties();
		
		try {
			pro.load(new FileInputStream("jyg.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		SERVER_PORT = Integer.parseInt(pro.getProperty("server.port"));
//		
//		SERVER_WEBSOCKET_PORT = Integer.parseInt(pro.getProperty("server.websocket.port"));
		String dir = pro.getProperty("http.root.dir");
		if(!dir.startsWith("/")) {
			dir = "/" + dir;
		}
		
		HTTP_ROOT_DIR = dir;
		
		//SOCKET_SO_LINGER = Integer.parseInt(pro.getProperty("socket.so_linger"));
		
	}
	
	
}

