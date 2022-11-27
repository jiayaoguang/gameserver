package org.jyg.gameserver.core.consumer;

import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.RemoteConsumerInfo;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2020/5/24
 */
public class RemoteGameConsumer extends ShareThreadGameConsumers {


    private final TcpClient tcpClient;

    private final List<RemoteDelegateGameConsumer> remoteDelegateGameConsumers = new ArrayList<>();

    private final Map<String,TcpClient> tcpClientMap = new LinkedHashMap<>(1024,0.75f);

    public RemoteGameConsumer(GameContext gameContext, String remoteHost , int port) {
        super(gameContext);

        String addr = remoteHost + ":"+ port;
        TcpClient tcpClient = tcpClientMap.get(addr);
        if(tcpClient == null){
            tcpClient = gameContext.createTcpClient(remoteHost,port);
            tcpClientMap.put(addr,tcpClient);
        }
        this.tcpClient = tcpClient;
    }

    public RemoteGameConsumer(GameContext gameContext) {
        super(gameContext);
        this.tcpClient = null;
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

    @Override
    public void onReciveEvent(EventData<?> event) {
        if(!isConnectAvailable()){
            tcpClient.checkConnect();
        }

        Object data = event.getData();

        if(isConnectAvailable()){
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

        try{
            update();
        }catch (Exception e){
            e.printStackTrace();
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
        if(isStart){
            throw new IllegalStateException();
        }


        GameContext gameContext = getGameContext();
        if(gameContext == null){
            throw new IllegalArgumentException("gameContext == null");
        }


        String addr = remoteConsumerInfo.getIp() + ":"+remoteConsumerInfo.getPort();
        TcpClient tcpClient = tcpClientMap.get(addr);
        if(tcpClient == null){
            tcpClient = gameContext.createTcpClient(remoteConsumerInfo.getIp(),remoteConsumerInfo.getPort());
            tcpClientMap.put(addr,tcpClient);
        }


        RemoteDelegateGameConsumer remoteDelegateGameConsumer = new RemoteDelegateGameConsumer(getGameContext(),tcpClient , remoteConsumerInfo);
        addRemoteConsumerInfo(remoteDelegateGameConsumer);
    }



}
