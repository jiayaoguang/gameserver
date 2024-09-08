package org.jyg.gameserver.core.event;

import io.netty.channel.Channel;

public class UdpMsgEvent extends MsgEvent<Object>{

    private final Channel channel;


    private final String senderHost;
    private final int senderPort;
    public UdpMsgEvent(int msgId, Object data, Channel channel, String senderHost,int senderPort) {
        super(msgId, data);
        this.channel = channel;
        this.senderHost = senderHost;
        this.senderPort = senderPort;
    }


    public Channel getChannel() {
        return channel;
    }

    public String getSenderHost() {
        return senderHost;
    }

    public int getSenderPort() {
        return senderPort;
    }
}
