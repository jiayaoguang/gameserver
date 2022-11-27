package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class ShareThreadGameConsumers extends MpscQueueGameConsumer {



    private final List<DelegateGameConsumer> delegateGameConsumers = new ArrayList<>();


    public ShareThreadGameConsumers(GameContext gameContext) {
        this.setGameContext(gameContext);

    }


    public ShareThreadGameConsumers(TcpClient tcpClient) {
        setGameContext(tcpClient.getGameContext());
    }


    @Override
    public void doStart() {
        super.doStart();


    }


    @Override
    public void doStop() {
        super.doStop();

    }

//    @Override
//    public void publicEvent(EventType evenType, Object data, Channel channel) {
//
//    }


//    @Override
//    public void publicEvent(EventType evenType, Object data, Channel channel, int eventId, EventExtData eventExtData) {
//        if(!isConnectAvailable()){
//            tcpClient.checkConnect();
//        }
//
//        if(isConnectAvailable()){
//            if(data instanceof ByteMsgObj){
//                tcpClient.write((ByteMsgObj)data);
//            }else if(data instanceof MessageLite){
//                tcpClient.write((MessageLite)data);
//            }else {
//                logger.error(" publicEvent fail , unknow date type {} ", data.getClass().getCanonicalName());
//            }
//        }else {
//            logger.error(" publicEvent fail , isConnectAvailable false ");
//        }
//    }


    public void addRemoteConsumerInfo(DelegateGameConsumer delegateGameConsumer){
        if(isStart){
            throw new IllegalStateException();
        }

        delegateGameConsumers.add(delegateGameConsumer);
        getGameContext().getConsumerManager().addConsumer(delegateGameConsumer);

    }



    @Override
    protected void run() {

        onThreadStart();

        int pollNullNum = 0;
        for (; !isStop(); ) {

            EventData<?> object = pollEvent();
            pollNullNum++;

            if (object != null) {
                try {
                    onReciveEvent(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pollNullNum = 0;
            }

            for (DelegateGameConsumer delegateGameConsumer : delegateGameConsumers) {
                if (delegateGameConsumer.tryPollEventAndDeal()) {
                    pollNullNum = 0;
                }
            }


            if (pollNullNum > 1000) {
                update();
                pollNullNum = 0;
                LockSupport.parkNanos(1000 * 1000L);
            } else if (pollNullNum > 800) {
                Thread.yield();
            }


        }
        Logs.DEFAULT_LOGGER.info(" stop.... ");
    }

    @Override
    public void update(){

        super.update();

        for( GameConsumer delegateGameConsumer : delegateGameConsumers ){
            delegateGameConsumer.update();
        }

    }



}
