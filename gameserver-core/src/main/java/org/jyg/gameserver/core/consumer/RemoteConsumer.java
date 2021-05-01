package org.jyg.gameserver.core.consumer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.timer.ITimerHandler;
import org.jyg.gameserver.core.timer.Timer;

import java.util.concurrent.TimeUnit;

/**
 * create by jiayaoguang on 2020/5/24
 */
public class RemoteConsumer extends Consumer {


    private final TcpClient tcpClient;
    private final String remoteAddress;
    private final int port;

    private Channel channel;

    public RemoteConsumer(TcpClient tcpClient, String remoteAddress , int port) {
        this.tcpClient = tcpClient;
        this.remoteAddress = remoteAddress;
        this.port = port;
    }

    @Override
    public void doStart() {
        connect();
        //定时检测重连 TODO think do it in other thread ?
        getContext().getDefaultConsumer().timerManager.addTimer(Integer.MAX_VALUE , TimeUnit.SECONDS.toMillis(5) , ()->{
            if(!isConnectAvailable()){
                logger.error("connect lose, try reconnect");
                connect();
            }
        });
    }

    private synchronized void connect() {
        if(channel != null){
            channel.close();
        }
        try {
            channel = tcpClient.connect(remoteAddress , port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if(channel != null){
            ChannelFuture closeFuture = channel.close();
            closeFuture.isSuccess();
        }
    }

//    @Override
//    public void publicEvent(EventType evenType, Object data, Channel channel) {
//
//    }

    @Override
    public void publicEvent(EventType evenType, Object data, Channel channel, int eventId) {
        if(!isConnectAvailable()){
            logger.error("  isConnectAvailable false , reconnect ");
            connect();
        }

        if(isConnectAvailable()){
//            channel.write(MsgBytes.newBuilder().build());
        }else {
            logger.error(" publicEvent fail , isConnectAvailable false ");
        }
    }

    private boolean isConnectAvailable(){
        if(channel == null){
            return false;
        }
        if(!channel.isActive()){
            return false;
        }
        return true;
    }

}
