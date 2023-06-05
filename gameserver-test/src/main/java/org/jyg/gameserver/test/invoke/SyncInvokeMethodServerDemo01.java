package org.jyg.gameserver.test.invoke;

import org.jyg.gameserver.core.annotaion.RemoteMethod;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.MpscQueueGameConsumer;
import org.jyg.gameserver.core.consumer.RemoteManagerGameConsumer;
import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.data.RemoteConsumerInfo;
import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.event.listener.GameEventListener;
import org.jyg.gameserver.core.exception.RequestTimeoutException;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.Logs;

/**
 * 非阻塞同步调用
 */
public class SyncInvokeMethodServerDemo01
{
    public static void main ( String[] args ) throws Exception 
    {

        GameConsumer mainGameConsumer = new MpscQueueGameConsumer();
        mainGameConsumer.setId(10);
    	
    	
    	GameServerBootstrap bootstarp = new GameServerBootstrap(mainGameConsumer);


        GameConsumer beInvokeConsumer = new BeInvokeConsumer();
        beInvokeConsumer.setId(10086);

        bootstarp.getGameContext().getConsumerManager().addConsumer(beInvokeConsumer);
        
        bootstarp.addHttpConnector(8080);

        bootstarp.addTcpConnector(9000);

        bootstarp.getGameContext().getMainGameConsumer().getEventManager().addEventListener(new GameEventListener<ConsumerThreadStartEvent>(){

            @Override
            public void onEvent(ConsumerThreadStartEvent consumerThreadStartEvent) {
//                consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethod(10086 , "sayHello" , new Object[0]);

                Object[] plusParams = {1,2};
                try {
                    Object result = consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethodAndWait(10086, "plus", plusParams);
                    Logs.DEFAULT_LOGGER.info("Synchronous non blocking get result : {} " , result);
                } catch (RequestTimeoutException e) {
                    throw new RuntimeException(e);
                }




            }
        });

        
        bootstarp.start();

    }





    public static class BeInvokeConsumer extends MpscQueueGameConsumer{

        @RemoteMethod(uname="plus")
        public int plus(int a,int b){
            Logs.CONSUMER.info("plus : {}", (a + b));

            return a+b;
        }

    }



}
