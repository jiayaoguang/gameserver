package com.jyg.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public class Constants {

	// websocket端口
	// public static final int SERVER_WEBSOCKET_PORT;
	// //http端口
	// public static final int SERVER_PORT;

	public static final String HTTP_ROOT_DIR;

	// public static final int SOCKET_SO_LINGER;
	static {
		Properties pro = new Properties();
		try (InputStream fis = Constants.class.getClassLoader().getResourceAsStream("jyg.properties");) {
			pro.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
			pro.put("http.root.dir", "/html");
		}

		String dir = pro.getProperty("http.root.dir");
		if (!dir.startsWith("/")) {
			dir = "/" + dir;
		}

		HTTP_ROOT_DIR = dir;

	}

}
