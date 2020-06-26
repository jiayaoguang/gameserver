package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.session.Session;
import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * create by jiayaoguang on 2020/5/3
 */
public class ChannelManager {

    private final Map<Channel, Session> channelObjectMap;

    private int sessionIdInc = 0;

    public ChannelManager() {
        this(new LinkedHashMap<>(1024 * 16));
    }

    public ChannelManager(Map<Channel, Session> channelObjectMap) {
        this.channelObjectMap = channelObjectMap;
    }

    //    public <T>void process(LogicEvent<T> event) {
//        if(event.getChannelEventType() == EventType.INNER_SOCKET_CONNECT_ACTIVE){
//            doLink(event);
//        }else if(event.getChannelEventType() == EventType.INNER_SOCKET_CONNECT_INACTIVE){
//            doUnlink(event);
//        }else {
//
//        }
//    }


    public final <T> void doLink(LogicEvent<T> event) {
        int sessionId = incAndGetSessionId();
        Session session = new Session(event.getChannel(), sessionId);
        channelObjectMap.put(event.getChannel(), session);
        afterLink(event);
    }

    public <T> void afterLink(LogicEvent<T> event) {

    }

    public final <T> void doUnlink(LogicEvent<T> event) {
        Session session = channelObjectMap.remove(event.getChannel());
        afterUnlink(session);
    }

    public <T> void afterUnlink(Session session) {

    }

    public Session getSession(Channel channel) {
        return channelObjectMap.get(channel);
    }


    /**
     * 检测并移除超时的channel
     */
    public void removeOutOfTimeChannels() {
//		System.out.println("检测并移除超时的channel");
        Iterator<Map.Entry<Channel, Session>> it = channelObjectMap.entrySet().iterator();
        for (; it.hasNext(); ) {
            Map.Entry<Channel, Session> entry = it.next();
            Channel channel = entry.getKey();
            Session session = entry.getValue();
            if (!channel.isOpen()) {
                it.remove();
                continue;
            }
            if (session == null) {
                it.remove();
                continue;
            }
            if ((session.getLastContactMill() + 60 * 1000L) < System.currentTimeMillis()) {
                channel.close();
                it.remove();
                System.out.println("移除超时的channel" + channel);
            }
        }
    }

    private int incAndGetSessionId() {
        sessionIdInc++;
        return sessionIdInc;
    }

}
