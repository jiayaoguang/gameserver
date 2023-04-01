package org.jyg.gameserver.core.event;

import io.netty.channel.Channel;

public class UnknownMsgEvent extends MsgEvent<byte[]> {


    private final Channel channel;

    public UnknownMsgEvent(int msgId, byte[] data, Channel channel) {
        super(msgId, data);
        this.channel = channel;
    }


    public Channel getChannel() {
        return channel;
    }
}
