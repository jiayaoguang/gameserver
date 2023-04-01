package org.jyg.gameserver.core.event;

import io.netty.channel.Channel;

public class NormalMsgEvent extends MsgEvent<Object>{

    private final Channel channel;

    public NormalMsgEvent(int msgId, Object data, Channel channel) {
        super(msgId, data);
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }
}
