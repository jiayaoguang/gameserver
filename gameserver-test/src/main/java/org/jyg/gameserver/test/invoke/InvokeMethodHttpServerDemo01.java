package org.jyg.gameserver.test.invoke;

import org.jyg.gameserver.core.annotaion.RemoteMethod;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.MpscQueueGameConsumer;
import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.event.listener.GameEventListener;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * 远程调用例子
 */
public class InvokeMethodHttpServerDemo01
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

        GameConsumer beInvokeConsumer = new BeInvokeConsumer();
        beInvokeConsumer.setId(10086);

        bootstarp.getGameContext().getConsumerManager().addConsumer(beInvokeConsumer);
        
        bootstarp.addHttpConnector(8080);

        bootstarp.getGameContext().getMainGameConsumer().getEventManager().addEventListener(new GameEventListener<ConsumerThreadStartEvent>(){

            @Override
            public void onEvent(ConsumerThreadStartEvent consumerThreadStartEvent) {
                consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethod(10086 , "sayHello" , new Object[0]);

                Object[] params = {1,2};
                consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethod(10086, "plus", params, new ResultHandler() {
                    @Override
                    public void call(int eventId, Object data) {
                        Logs.CONSUMER.info("get plus result : {}", data);
                    }
                });

            }
        });

        
        bootstarp.start();
    }


    public static class BeInvokeConsumer extends MpscQueueGameConsumer{
        @RemoteMethod
        public void sayHello(){
            Logs.CONSUMER.info("hello");
        }

        @RemoteMethod(uname="plus")
        public int plus(int a,int b){
            Logs.CONSUMER.info("plus : {}", (a + b));

            return a+b;
        }

    }

}
