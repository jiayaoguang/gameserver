package org.gameserver.auth;

import com.jyg.net.HttpProcessor;
import com.jyg.net.HttpService;
import com.jyg.net.Request;
import com.jyg.net.Response;
import com.jyg.startup.Server;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main ( String[] args ) throws Exception 
    {
    	
        Server bootstarp = new Server();
        
        bootstarp.registerHttpEvent("/index", new IndexHttpProcessor());
        
        bootstarp.registerHttpEvent("/login", new LoginHttpProcessor());
        
        bootstarp.registerHttpEvent("/loginhtml", new LoginHtmlHttpProcessor());
        
        bootstarp.addService(new HttpService(8080,true));
        
        bootstarp.start();
    }
}
