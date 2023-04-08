package org.jyg.gameserver.core.consumer;

import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.RemoteConsumerInfo;
import org.jyg.gameserver.core.event.Event;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.util.GameContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * create by jiayaoguang on 2020/5/24
 */
public class RemoteGameConsumer extends MpscQueueGameConsumer {


    /**
     * TODO 考虑要不要去掉
     */
    @Deprecated
    private final TcpClient tcpClient;


    private final Map<String,TcpClient> tcpClientMap = new LinkedHashMap<>(1024,0.75f);

    public RemoteGameConsumer(GameContext gameContext, String remoteHost , int port) {
        this.setGameContext(gameContext);

        String addr = remoteHost + ":"+ port;
        TcpClient tcpClient = tcpClientMap.get(addr);
        if(tcpClient == null){
            tcpClient = gameContext.createTcpClient(remoteHost,port);
            tcpClientMap.put(addr,tcpClient);
        }
        this.tcpClient = tcpClient;
        this.setConsumerThread(new QueueConsumerThread(this));
    }

    public RemoteGameConsumer(GameContext gameContext) {
        this(gameContext,null);
    }


    private RemoteGameConsumer(GameContext gameContext,TcpClient tcpClient) {
        this.setGameContext(gameContext);
        this.tcpClient = tcpClient;
        this.setConsumerThread(new QueueConsumerThread(this));
    }

//    public RemoteGameConsumer(TcpClient tcpClient) {
//        super(tcpClient.getGameContext());
//        this.tcpClient = tcpClient;
//
//    }


    @Override
    public void doStart() {
        super.doStart();

        for(TcpClient tcpClient : tcpClientMap.values()){

            try{
                tcpClient.checkConnect();
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        this.timerManager.addUnlimitedTimer( TimeUnit.SECONDS.toMillis(5) , ()->{
            for(TcpClient tcpClient : tcpClientMap.values()){
                try{
                    tcpClient.checkConnect();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        });

    }


    @Override
    public void doStop() {
        super.doStop();


        for(TcpClient tcpClient : tcpClientMap.values()){
            tcpClient.close();
        }

//        if(tcpClient.getGameContext() == getGameContext()){
//            tcpClient.close();
//        }else {
//            tcpClient.stop();
//        }

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

//    @Override
//    public void onReciveEvent(EventData<?> event) {
//        if(!isConnectAvailable()){
//            tcpClient.checkConnect();
//        }
//
//        Object data = event.getData();
//        if(event.getEvent() instanceof MsgEvent){
//            if(isConnectAvailable()){
//                if(data instanceof ByteMsgObj){
//                    tcpClient.write((ByteMsgObj)data);
//                }else if(data instanceof MessageLite){
//                    tcpClient.write((MessageLite)data);
//                }else {
//                    logger.error(" publicEvent fail , unknow date type {} ", data.getClass().getCanonicalName());
//                }
//            }else {
//                logger.error(" publicEvent fail , isConnectAvailable false ");
//            }
//        }else {
//
//        }
//
//        try{
//            update();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }


    @Override
    protected void doEvent(EventData eventData) {
        if(!isConnectAvailable()){
            tcpClient.checkConnect();
        }

        Event event = eventData.getEvent();
        if(event instanceof MsgEvent){
            if(isConnectAvailable()){
                Object data = ((MsgEvent<?>) event).getMsgData();
                if(data instanceof ByteMsgObj){
                    tcpClient.write((ByteMsgObj)data);
                }else if(data instanceof MessageLite){
                    tcpClient.write((MessageLite)data);
                }else {
                    logger.error(" publicEvent fail , unknow date type {} ", data.getClass().getCanonicalName());
                }
            }else {
                logger.error(" publicEvent fail , isConnectAvailable false ");
            }
        }else {
            getEventManager().publishEvent(eventData.getEvent());
        }

    }


    private boolean isConnectAvailable(){
        return tcpClient.isConnectAvailable();
    }

    public void addRemoteConsumerInfo(int consumerId , String ip , int port){
        RemoteConsumerInfo remoteConsumerInfo = new RemoteConsumerInfo();
        remoteConsumerInfo.setConsumerId(consumerId);
        remoteConsumerInfo.setIp(ip);
        remoteConsumerInfo.setPort(port);
        addRemoteConsumerInfo(remoteConsumerInfo);
    }

    public void addRemoteConsumerInfo(RemoteConsumerInfo remoteConsumerInfo){
        if(isStart()){
            throw new IllegalStateException();
        }

        TcpClient tcpClient = getOrCreateTcpClient(remoteConsumerInfo.getIp() ,remoteConsumerInfo.getPort());

        RemoteDelegateGameConsumer remoteDelegateGameConsumer = new RemoteDelegateGameConsumer(getGameContext() , this.getConsumerThread(),tcpClient , remoteConsumerInfo);
        getGameContext().getConsumerManager().addConsumer(remoteDelegateGameConsumer);
    }


    private TcpClient getOrCreateTcpClient(String ip,int port){
        GameContext gameContext = getGameContext();
        if(gameContext == null){
            throw new IllegalArgumentException("gameContext == null");
        }
        String addr = ip + ":"+port;
        TcpClient tcpClient = tcpClientMap.get(addr);
        if(tcpClient == null){
            tcpClient = gameContext.createTcpClient(ip,port);
            tcpClientMap.put(addr,tcpClient);
        }
        return tcpClient;
    }



}
