package org.jyg.gameserver.test.invoke;

import org.jyg.gameserver.core.annotaion.RemoteMethod;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.MpscQueueGameConsumer;
import org.jyg.gameserver.core.consumer.RemoteManagerGameConsumer;
import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.data.RemoteConsumerInfo;
import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.event.listener.GameEventListener;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.GameContext;
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

        GameConsumer mainGameConsumer = new MpscQueueGameConsumer();
        mainGameConsumer.setId(10);
    	
    	
    	GameServerBootstrap bootstarp = new GameServerBootstrap(mainGameConsumer);

        bootstarp.addHttpProcessor(new HttpProcessor("/index") {
			
			@Override
			public void service(Request request, Response response) {

				response.writeAndFlush( "<html><head></head><body><div align='center'><h1>Hello world!</h1></div><body></html>" );
				
			}
		} );

        GameConsumer beInvokeConsumer = new BeInvokeConsumer();
        beInvokeConsumer.setId(10086);
        beInvokeConsumer.putInstance(TestInvoke.class);

        bootstarp.getGameContext().getConsumerManager().addConsumer(beInvokeConsumer);
        
        bootstarp.addHttpConnector(8080);

        bootstarp.addTcpConnector(9000);

        bootstarp.getGameContext().getMainGameConsumer().getEventManager().addEventListener(new GameEventListener<ConsumerThreadStartEvent>(){

            @Override
            public void onEvent(ConsumerThreadStartEvent consumerThreadStartEvent) {
                consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethod(10086 , "sayHello" , new Object[0]);

                Object[] plusParams = {1,2};
                consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethod(10086, "plus", plusParams, new ResultHandler() {
                    @Override
                    public void call(int eventId, Object data) {
                        Logs.CONSUMER.info("get plus result : {}", data);
                    }
                });


                Object[] appendParams = {"aaa","bbb"};
                consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethod(10086, "append", appendParams, new ResultHandler() {
                    @Override
                    public void call(int eventId, Object data) {
                        Logs.CONSUMER.info("get append result : {}", data);
                    }
                });

            }
        });

        
        bootstarp.start();

        callRemotePortMethod();
    }



    public static void callRemotePortMethod(){


        GameServerBootstrap bootstrap = new GameServerBootstrap();


        bootstrap.addTcpConnector(9002);

        RemoteConsumerInfo remoteConsumerInfo = new RemoteConsumerInfo();
        remoteConsumerInfo.setPort(9000);
        remoteConsumerInfo.setConsumerId(10086);
        remoteConsumerInfo.setIp("127.0.0.1");

        RemoteManagerGameConsumer remoteManagerGameConsumer = new RemoteManagerGameConsumer(bootstrap.getGameContext() , remoteConsumerInfo);


        bootstrap.getGameContext().getConsumerManager().addConsumer(remoteManagerGameConsumer);

        bootstrap.getGameContext().getMainGameConsumer().getEventManager().addEventListener(new GameEventListener<ConsumerThreadStartEvent>(){

            @Override
            public void onEvent(ConsumerThreadStartEvent consumerThreadStartEvent) {



                consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethod(10086 , "sayHello" , new Object[0]);

                Object[] plusParams = {1,2};
                consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethod(10086, "plus", plusParams, new ResultHandler() {
                    @Override
                    public void call(int eventId, Object data) {
                        Logs.CONSUMER.info("get remote plus result : {}", data);
                    }
                });


                Object[] appendParams = {"aaa","bbb"};
                consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().invokeRemoteMethod(10086, "append", appendParams, new ResultHandler() {
                    @Override
                    public void call(int eventId, Object data) {
                        Logs.CONSUMER.info("get remote append result : {}", data);
                    }
                });

            }
        });


        bootstrap.start();
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



    public static class TestInvoke {
        @RemoteMethod(uname="append")
        public String append(String a,String b){
            Logs.CONSUMER.info("String : {} {}",a , b);

            return a+b;
        }
    }

}
