package org.jyg.gameserver.core.consumer;

import io.netty.channel.Channel;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.proto.MsgBytes;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.timer.ITimerHandler;
import org.jyg.gameserver.core.timer.Timer;

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
    public void start() {
        connect();
    }

    private void connect() {
        try {
            channel = tcpClient.connect(remoteAddress , port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void publicEvent(EventType evenType, Object data, Channel channel) {
        channel.write(MsgBytes.newBuilder().build());
    }

    @Override
    public void publicEvent(EventType evenType, Object data, Channel channel, int eventId) {
        channel.write(MsgBytes.newBuilder().build());
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
