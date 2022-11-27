package org.jyg.gameserver.core.event;

import io.netty.channel.Channel;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.net.Request;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class HttpRequestEvent implements Event{

    private final Request request;
    private final Channel channel;

    private final EventData<Request> eventData;


    public HttpRequestEvent(Request request, Channel channel, EventData<Request> eventData) {
        this.request = request;
        this.channel = channel;
        this.eventData = eventData;
    }


    public Request getRequest() {
        return request;
    }

    public Channel getChannel() {
        return channel;
    }

    public EventData<Request> getEventData() {
        return eventData;
    }
}
