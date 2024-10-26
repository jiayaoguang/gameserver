package org.jyg.gameserver.test.invoke;

import org.jyg.gameserver.core.annotaion.InvokeRemoteMethod;
import org.jyg.gameserver.core.annotaion.RemoteMethod;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.MpscQueueGameConsumer;
import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.event.listener.GameEventListener;
import org.jyg.gameserver.core.manager.InvokeRemoteResultFuture;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.Logs;

/**
 * 如果方法返回类型是InvokeRemoteResultFuture
 * 收到远程处理后的结果后会调用InvokeRemoteResultFuture的ResultHandler
 * 需要给返回值设置获取结果后的处理逻辑
 */
public class ProxyInvokeMethodFutureServerDemo01 {
    public static void main(String[] args) throws Exception {

        GameConsumer mainGameConsumer = new MpscQueueGameConsumer();
        mainGameConsumer.setId(10);


        GameServerBootstrap bootstarp = new GameServerBootstrap(mainGameConsumer);

        GameConsumer beInvokeConsumer = new MpscQueueGameConsumer();
        beInvokeConsumer.setId(10086);
        beInvokeConsumer.putInstance(PlusManager.class);

        bootstarp.getGameContext().getConsumerManager().addConsumer(beInvokeConsumer);

        bootstarp.addTcpConnector(8091);


        bootstarp.getGameContext().getMainGameConsumer().getEventManager().addEventListener(new GameEventListener<ConsumerThreadStartEvent>() {

            @Override
            public void onEvent(ConsumerThreadStartEvent consumerThreadStartEvent) {


                PlusManagerProxy plusManagerProxy = consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().getOrCreateRemoteMethodProxy(PlusManagerProxy.class);

                //发起远程调用
                InvokeRemoteResultFuture<Integer> resultFuture = plusManagerProxy.plus(100, 200);


                //设置获取结果后的处理逻辑
                resultFuture.setResultHandler(new ResultHandler<Integer>() {
                    @Override
                    public void call(int eventId, Integer result) {
                        Logs.DEFAULT_LOGGER.info("get plus result : {}" , result);
                    }
                });

            }
        });


        bootstarp.start();

    }


    public static interface PlusManagerProxy {

        @InvokeRemoteMethod(uname = "plus", targetConsumerId = 10086)
        public InvokeRemoteResultFuture<Integer> plus(int a, int b);


    }


    public static class PlusManager {

        @RemoteMethod(uname = "plus")
        public int plus(int a, int b) {
            Logs.CONSUMER.info("plus : {}", (a + b));

            return a + b;
        }

    }
}
