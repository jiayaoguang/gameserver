package com.jyg.tcp.http;

import com.jyg.net.HttpProcessor;
import com.jyg.net.HttpService;
import com.jyg.net.Request;
import com.jyg.net.Response;
import com.jyg.startup.GameServerBootstarp;

/**
 * Hello world!
 * http例子
 */
public class HttpServerDemo01
{
    public static void main ( String[] args ) throws Exception 
    {
    	
    	
    	GameServerBootstarp bootstarp = new GameServerBootstarp();
        
        bootstarp.registerHttpEvent("/index", new HttpProcessor() {
			
			@Override
			public void service(Request request, Response response) {

				response.writeAndFlush( "<html><head></head><body><div align='center'><h1>Hello world!</h1></div><body></html>" );
				
			}
		} );
        
        bootstarp.addService(new HttpService(8080));
        
        bootstarp.start();
    }
}
