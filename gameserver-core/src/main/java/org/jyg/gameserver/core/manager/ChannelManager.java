package org.jyg.gameserver.core.manager;

import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.event.ConnectEvent;
import org.jyg.gameserver.core.event.DisconnectEvent;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.session.TcpChannelSession;
import org.jyg.gameserver.core.session.Session;
import io.netty.channel.Channel;
import org.jyg.gameserver.core.util.Logs;

import java.util.*;

/**
 * create by jiayaoguang on 2020/5/3
 */
public class ChannelManager implements Lifecycle {

    private final Map<Channel, Session> channelObjectMap;

    private final Map<Long, Session> id2sessionMap;

    private final Map<Channel, Session> tcpClientChannelObjectMap;

    private long sessionIdInc = 0;

    private final Consumer consumer;

    public ChannelManager(Consumer consumer) {
        this.channelObjectMap = new LinkedHashMap<>(1024 * 16 , 0.5f);

        this.tcpClientChannelObjectMap = new LinkedHashMap<>(32 , 0.5f);

        this.id2sessionMap = new LinkedHashMap<>(32 , 0.5f);

        this.consumer = consumer;
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


    public final <T> Session doLink(Channel channel) {
        long sessionId = incAndGetSessionId();
        Session session = new TcpChannelSession(channel, sessionId);
        channelObjectMap.put(channel, session);
        id2sessionMap.put(session.getSessionId() , session);
        afterConnect(session);
        return session;
    }

    public <T> void afterConnect(Session session) {
        consumer.getEventManager().triggerEvent(ConnectEvent.class,session);
    }

    public final <T> void doUnlink(Channel channel) {
        Session session = channelObjectMap.remove(channel);
        if(channel.isOpen()){
            channel.close();
        }
        afterUnlink(session);
    }

    public <T> void afterUnlink(Session session) {
        consumer.getEventManager().triggerEvent(DisconnectEvent.class,session);
    }



    public final <T> Session doTcpClientLink(Channel channel) {
        long sessionId = incAndGetSessionId();
        Session session = new TcpChannelSession(channel, sessionId);
        tcpClientChannelObjectMap.put(channel, session);
//        id2sessionMap.put(session.getSessionId() , session);
        afterConnect(session);
        return session;
    }


    public final <T> void doTcpClientUnlink(Channel channel) {
        Session session = tcpClientChannelObjectMap.remove(channel);
        if(session != null){
            id2sessionMap.remove(session.getSessionId());
        }
        afterUnlink(session);
    }

//    public Session getTcpClientSession(Channel channel) {
//        return tcpClientChannelObjectMap.get(channel);
//    }

    public Session getSession(Channel channel) {

        Session session = channelObjectMap.get(channel);
        if(session == null){
            session = tcpClientChannelObjectMap.get(channel);
        }
        return session;
    }

    public Session getSession(long sessionId) {

        Session session = id2sessionMap.get(sessionId);

        return session;
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
                Logs.DEFAULT_LOGGER.info("移除超时的channel" + channel);
            }
        }
    }

    private long incAndGetSessionId() {
        sessionIdInc++;
        return sessionIdInc;
    }


    @Deprecated
    public void broadcast(ByteMsgObj byteMsgObj){
        for(Channel channel : channelObjectMap.keySet()){
            channel.writeAndFlush(byteMsgObj);
        }
    }

    @Deprecated
    public void broadcast(MessageLite protoMessage){
        for(Channel channel : channelObjectMap.keySet()){
            channel.writeAndFlush(protoMessage);
        }
    }

    public List<Session> getSessions(){
        return new ArrayList<>(channelObjectMap.values());
    }

    public int getChannelCount(){
        return channelObjectMap.size() + tcpClientChannelObjectMap.size();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

        for(Session session : channelObjectMap.values()){
            session.stop();
        }

    }

}
