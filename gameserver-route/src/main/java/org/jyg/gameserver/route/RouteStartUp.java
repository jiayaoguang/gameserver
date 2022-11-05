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


    	GameServerBootstrap bootstarp = new GameRouteBootstarp();
//	    RedisCacheClient redisCacheClient = injector.getInstance(RedisCacheClient.class);
//	    redisCacheClient.init();

//        bootstarp.registerHttpEvent("/index000", injector.getInstance(TokenSendHttpProcessor.class));


//        bootstarp.addProtoProcessor(1000, injector.getInstance(LoginProtoProcessor.class));


        bootstarp.addHttpConnector(8082);

        bootstarp.addTcpConnector(8081);



        bootstarp.start();
        logger.info(" start success ");
    }
}
