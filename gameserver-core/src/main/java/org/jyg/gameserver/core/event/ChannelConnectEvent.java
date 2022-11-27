package org.jyg.gameserver.core.event;

import io.netty.channel.Channel;
import org.jyg.gameserver.core.event.Event;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class ChannelConnectEvent implements Event {

    private final Channel channel;

    public ChannelConnectEvent(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }
}
