package org.gameserver.sm;

import com.jyg.net.RpcService;
import com.jyg.net.Service;
import com.jyg.startup.Bootstarp;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        Bootstarp bootstarp = new Bootstarp();
        
        Service rpcService = new RpcService(10000);
        
        bootstarp.addService(rpcService);
        
        bootstarp.start();
    }
}
