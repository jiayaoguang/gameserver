package org.jyg.gameserver.test.tcp.http;

import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.startup.GameServerBootstrap;

/**
 * Hello world!
 * http例子
 */
public class HttpServerDemo01
{
    public static void main ( String[] args ) throws Exception 
    {
    	
    	
    	GameServerBootstrap bootstarp = new GameServerBootstrap();
        
        bootstarp.addHttpProcessor(new HttpProcessor("/index") {
			
			@Override
			public void service(Request request, Response response) {

				response.writeAndFlush( "<html><head></head><body><div align='center'><h1>Hello world!</h1></div><body></html>" );
				
			}
		} );
        
        bootstarp.addHttpConnector(8080);
        
        bootstarp.start();
    }
}
