package org.jyg.gameserver.core.consumer;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.util.Context;

import java.util.concurrent.TimeUnit;

/**
 * create by jiayaoguang on 2020/5/24
 */
public class RemoteConsumer extends MpscQueueConsumer {


    private final TcpClient tcpClient;


    public RemoteConsumer(Context context, String remoteHost , int port) {
        this.setContext(context);
        this.tcpClient = context.createTcpClient(remoteHost , port);
    }

    public RemoteConsumer(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public void beforeStart() {
        tcpClient.start();
        //定时检测重连 TODO think do it in other thread ?
        this.timerManager.addTimer(Integer.MAX_VALUE , TimeUnit.SECONDS.toMillis(5) , ()->{
            if(!tcpClient.isConnectAvailable()){
                tcpClient.checkConnect();
            }
        });
    }


    @Override
    public void doStop() {

        if(tcpClient.getContext() == getContext()){
            tcpClient.close();
        }else {
            tcpClient.stop();
        }

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

    }

    private boolean isConnectAvailable(){
        return tcpClient.isConnectAvailable();
    }

}
