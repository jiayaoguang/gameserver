package org.jyg.gameserver.core.event;

import io.netty.channel.Channel;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.net.Request;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class HttpRequestEvent extends MsgEvent<Request>{

    private final Channel channel;



    public HttpRequestEvent(Request request, Channel channel) {
        super(0,request);
        this.channel = channel;
    }



    public Channel getChannel() {
        return channel;
    }

}
