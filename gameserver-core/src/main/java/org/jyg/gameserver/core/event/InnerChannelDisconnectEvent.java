package org.jyg.gameserver.core.event;

import io.netty.channel.Channel;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class InnerChannelDisconnectEvent extends Event {

    private final Channel channel;

    public InnerChannelDisconnectEvent(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }
}
