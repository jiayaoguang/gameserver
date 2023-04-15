package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.RemoteConsumerInfo;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.util.GameContext;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * create by jiayaoguang on 2020/5/24
 */
public class RemoteManagerGameConsumer extends RemoteDelegateGameConsumer {


    private final Map<String,TcpClient> tcpClientMap = new LinkedHashMap<>(1024,0.75f);


    public RemoteManagerGameConsumer(GameContext gameContext, QueueConsumerThread queueConsumerThread , RemoteConsumerInfo remoteConsumerInfo) {
        super(gameContext,queueConsumerThread ,gameContext.createTcpClient(remoteConsumerInfo.getIp(),remoteConsumerInfo.getPort()) , remoteConsumerInfo );
        addTcpClient(getTcpClient());
    }


    public RemoteManagerGameConsumer(GameContext gameContext , RemoteConsumerInfo remoteConsumerInfo,String consumerThreadName) {
        super(gameContext,new QueueConsumerThread(consumerThreadName) ,gameContext.createTcpClient(remoteConsumerInfo.getIp(),remoteConsumerInfo.getPort()) , remoteConsumerInfo );

        addTcpClient(getTcpClient());
    }

    public RemoteManagerGameConsumer(GameContext gameContext , RemoteConsumerInfo remoteConsumerInfo) {
        this(gameContext , remoteConsumerInfo , "RemoteManagerGameConsumer");
    }


    public RemoteManagerGameConsumer(GameContext gameContext , QueueConsumerThread queueConsumerThread , TcpClient tcpClient, RemoteConsumerInfo remoteConsumerInfo) {
        super(gameContext,queueConsumerThread ,tcpClient , remoteConsumerInfo );
        addTcpClient(getTcpClient());
    }

//    public RemoteGameConsumer(GameContext gameContext , TcpClient tcpClient, RemoteConsumerInfo remoteConsumerInfo) {
//        super(gameContext,new QueueConsumerThread() ,tcpClient , remoteConsumerInfo );
//    }



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


    public void addTcpClient(TcpClient tcpClient){
        String ip = tcpClient.getHost();
        int port = tcpClient.getPort();
        String addr = ip + ":"+port;
        TcpClient oldTcpClient = tcpClientMap.get(addr);
        if(oldTcpClient != null){
            throw new IllegalArgumentException("oldTcpClient != null");
        }
        tcpClientMap.put(addr,tcpClient);
    }



}
