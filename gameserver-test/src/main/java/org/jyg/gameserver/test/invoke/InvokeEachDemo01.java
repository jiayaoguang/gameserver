package org.jyg.gameserver.test.invoke;

import org.jyg.gameserver.core.annotaion.RemoteMethod;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.MpscQueueGameConsumer;
import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.event.listener.GameEventListener;
import org.jyg.gameserver.core.exception.RequestTimeoutException;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.timer.ITimerHandler;
import org.jyg.gameserver.core.util.Logs;

/**
 * 客户端调用服务器的方法，并在服务器添加 consumer和连接的映射关系
 * 之后服务器就调用客户端的方法了
 */
public class InvokeEachDemo01
{
    public static void main ( String[] args ) throws Exception 
    {

        GameConsumer mainGameConsumer = new MpscQueueGameConsumer();
        mainGameConsumer.setId(10);
    	
    	
    	GameServerBootstrap bootstarp = new GameServerBootstrap(mainGameConsumer);


        bootstarp.getGameContext().getMainGameConsumer().getInstanceManager().putInstance(ServerBeInvoke.class);


//        bootstarp.addHttpConnector(8080);

        bootstarp.addTcpConnector(9000);

//        bootstarp.getGameContext().getMainGameConsumer().getEventManager().addEventListener(new GameEventListener<ConsumerThreadStartEvent>(){
//
//            @Override
//            public void onEvent(ConsumerThreadStartEvent consumerThreadStartEvent) {
////                consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethod(10086 , "sayHello" , new Object[0]);
//
//                Object[] plusParams = {1,2};
//                try {
//                    Object result = consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethodAndWait(10086, "plus", plusParams);
//                    Logs.DEFAULT_LOGGER.info("Synchronous non blocking get result : {} " , result);
//                } catch (RequestTimeoutException e) {
//                    throw new RuntimeException(e);
//                }
//
//
//
//
//            }
//        });


        mainGameConsumer.getTimerManager().addTimer(1, 100L, new ITimerHandler() {
            @Override
            public void onTime() {
                try {
                    Object result = mainGameConsumer.getRemoteMethodInvokeManager().invokeRemoteMethodAndWait(10, "helloServer");
                    Logs.DEFAULT_LOGGER.info("client Synchronous non blocking get result : {} " , result);
                } catch (RequestTimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        
        bootstarp.start();


        startClient();




    }




    public static void startClient (  ) throws Exception
    {

        GameConsumer mainGameConsumer = new MpscQueueGameConsumer();
        mainGameConsumer.setId(100);


        TcpClient client = new TcpClient(mainGameConsumer,"127.0.0.1",9000);



        client.getGameContext().getMainGameConsumer().getInstanceManager().putInstance(ClientBeInvoke.class);


        client.getGameContext().getMainGameConsumer().getEventManager().addEventListener(new GameEventListener<ConsumerThreadStartEvent>(){

            @Override
            public void onEvent(ConsumerThreadStartEvent consumerThreadStartEvent) {
//                consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethod(10086 , "sayHello" , new Object[0]);

                try {
                    Object result = consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethodAndWait(100, "helloClient");
                    Logs.DEFAULT_LOGGER.info("server Synchronous non blocking get result : {} " , result);
                } catch (RequestTimeoutException e) {
                    throw new RuntimeException(e);
                }




            }
        });


        client.start();

    }



    public static class ServerBeInvoke{

        @RemoteMethod(uname="helloServer")
        public String helloServer(){
//            Logs.CONSUMER.info("helloServer ");

            return "helloServer";
        }

    }



    public static class ClientBeInvoke{

        @RemoteMethod(uname="helloClient")
        public String helloClient(){
            Logs.CONSUMER.info("helloClient ");

            return "helloClient";
        }

    }



}
