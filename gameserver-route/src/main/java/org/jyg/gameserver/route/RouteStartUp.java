package org.jyg.gameserver.route;

import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Hello world!
 *
 */
public class RouteStartUp
{

	private static final Logger logger = LoggerFactory.getLogger(RouteStartUp.class);

    public static void main ( String[] args ) throws Exception
    {


        GameRouteBootstarp bootstarp = new GameRouteBootstarp();
//	    RedisCacheClient redisCacheClient = injector.getInstance(RedisCacheClient.class);
//	    redisCacheClient.init();

//        bootstarp.registerHttpEvent("/index000", injector.getInstance(TokenSendHttpProcessor.class));


//        bootstarp.addProtoProcessor(1000, injector.getInstance(LoginProtoProcessor.class));


        RouteConfig routeConfig = bootstarp.getRouteConfig();
        if(routeConfig.getRouteHttpPort() > 0){
            bootstarp.addHttpConnector(routeConfig.getRouteHttpPort());
        }

        bootstarp.addTcpConnector(routeConfig.getRouteTcpPort());



        bootstarp.start();
        logger.info(" start success ");
    }
}
