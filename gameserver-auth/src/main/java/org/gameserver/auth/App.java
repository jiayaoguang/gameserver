package org.gameserver.auth;

import com.jyg.net.HttpProcessor;
import com.jyg.net.HttpService;
import com.jyg.net.Request;
import com.jyg.net.Response;
import com.jyg.startup.Bootstarp;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main ( String[] args ) throws Exception 
    {
    	
        Bootstarp bootstarp = new Bootstarp();
        
        bootstarp.registerHttpEvent("/index", new IndexHttpProcessor());
        
        bootstarp.registerHttpEvent("/login", new LoginHttpProcessor());
        
        bootstarp.addService(new HttpService(8080,true));
        
        bootstarp.start();
    }
}
