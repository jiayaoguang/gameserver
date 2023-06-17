package org.jyg.gameserver.test.invoke;

import org.jyg.gameserver.core.annotaion.RemoteMethod;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.MpscQueueGameConsumer;
import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.event.listener.GameEventListener;
import org.jyg.gameserver.core.exception.RequestTimeoutException;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.Logs;

/**
 * 代理类非阻塞同步调用
 */
public class ProxySyncInvokeMethodServerDemo01
{
    public static void main ( String[] args ) throws Exception 
    {

        GameConsumer mainGameConsumer = new MpscQueueGameConsumer();
        mainGameConsumer.setId(10);
    	
    	
    	GameServerBootstrap bootstarp = new GameServerBootstrap(mainGameConsumer);


        GameConsumer beInvokeConsumer = new MpscQueueGameConsumer();
        beInvokeConsumer.setId(10086);
        beInvokeConsumer.putInstance(PlusManager.class);

        bootstarp.getGameContext().getConsumerManager().addConsumer(beInvokeConsumer);

        bootstarp.addHttpConnector(8080);

        bootstarp.addTcpConnector(9000);

        bootstarp.getGameContext().getMainGameConsumer().getEventManager().addEventListener(new GameEventListener<ConsumerThreadStartEvent>(){

            @Override
            public void onEvent(ConsumerThreadStartEvent consumerThreadStartEvent)  {


                InvokeProxyManager invokeProxyManager = new InvokeProxyManager(consumerThreadStartEvent.getGameConsumer());
                PlusManager beInvokeConsumerProxy = null;
                try {
                    beInvokeConsumerProxy = invokeProxyManager.createProxy(PlusManager.class , 10086);
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }

                //发起远程调用
                int result = beInvokeConsumerProxy.plus(100,200);

                System.out.println("get plus result : "+result);


                //发起远程调用
                int plusAndDoubleResult = beInvokeConsumerProxy.plusAndDouble(100,200);

                System.out.println("get plusAndDouble result : "+plusAndDoubleResult);



//                Object[] plusParams = {1,2};
//                try {
//                    Object result = consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethodAndWait(10086, "plus", plusParams);
//                    Logs.DEFAULT_LOGGER.info("Synchronous non blocking get result : {} " , result);
//                } catch (RequestTimeoutException e) {
//                    throw new RuntimeException(e);
//                }




            }
        });

        
        bootstarp.start();

    }







    public static class PlusManager{

        @RemoteMethod(uname="plus")
        public int plus(int a,int b){
            Logs.CONSUMER.info("plus : {}", (a + b));

            return a+b;
        }

        @RemoteMethod()
        public int plusAndDouble(int a,int b){
            Logs.CONSUMER.info("plusAndDouble : {}", (a + b) * 2);

            return (a+b) * 2;
        }

    }
}
