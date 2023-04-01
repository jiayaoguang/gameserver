package org.jyg.gameserver.core.event;

import io.netty.channel.Channel;
import org.jyg.gameserver.core.data.EventData;

/**
 * create by jiayaoguang on 2022/11/27
 */
@Deprecated
public class ChannelMsgEvent extends Event {

    private final Channel channel;

    private final EventData<?> eventData;

    public ChannelMsgEvent(Channel channel, EventData<?> eventData) {
        this.channel = channel;
        this.eventData = eventData;
    }

    public Channel getChannel() {
        return channel;
    }

    public EventData<?> getEventData() {
        return eventData;
    }
}
