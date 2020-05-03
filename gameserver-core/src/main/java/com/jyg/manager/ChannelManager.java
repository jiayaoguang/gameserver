package com.jyg.manager;

import com.jyg.bean.LogicEvent;
import com.jyg.enums.EventType;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * create by jiayaoguang on 2020/5/3
 */
public class ChannelManager<T> {

    private Map<Channel , Object> channelObjectMap = new HashMap<>(1024);

    public void process(LogicEvent<T> event) {
        if(event.getChannelEventType() == EventType.INNER_SOCKET_CONNECT_ACTIVE){
            doLink(event);
        }else if(event.getChannelEventType() == EventType.INNER_SOCKET_CONNECT_INACTIVE){
            doUnlink(event);
        }else {

        }
    }

    public final void doLink(LogicEvent<T> event){
        channelObjectMap.put(event.getChannel() , "");
        afterLink(event);
    }

    public void afterLink(LogicEvent<T> event){

    }

    public final void doUnlink(LogicEvent<T> event){
        channelObjectMap.remove(event.getChannel());
        afterUnlink(event);
    }

    public void afterUnlink(LogicEvent<T> event){

    }
}
