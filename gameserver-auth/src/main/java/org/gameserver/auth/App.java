package org.gameserver.auth;

import com.jyg.net.HttpService;
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
        
        bootstarp.registerHttpEvent("index", null);
        
        bootstarp.addService(new HttpService(8080));
        bootstarp.start();
    }
}
