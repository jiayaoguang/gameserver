package org.jyg.gameserver.core.manager;

import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.session.Session;
import io.netty.channel.Channel;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Logs;

import java.util.*;

/**
 * create by jiayaoguang on 2020/5/3
 */
public class ChannelManager implements Lifecycle {

    private final Map<Channel, Session> channelObjectMap;

    private final Map<Channel, Session> tcpClientChannelObjectMap;

    private int sessionIdInc = 0;

    public ChannelManager() {
        this.channelObjectMap = new LinkedHashMap<>(1024 * 16 , 0.5f);

        this.tcpClientChannelObjectMap = new LinkedHashMap<>(32 , 0.5f);
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
        int sessionId = incAndGetSessionId();
        Session session = new Session(channel, sessionId);
        channelObjectMap.put(channel, session);
        afterLink(session);
        return session;
    }

    public <T> void afterLink(Session session) {

    }

    public final <T> void doUnlink(Channel channel) {
        Session session = channelObjectMap.remove(channel);
        afterUnlink(session);
    }

    public <T> void afterUnlink(Session session) {

    }



    public final <T> Session doTcpClientLink(Channel channel) {
        int sessionId = incAndGetSessionId();
        Session session = new Session(channel, sessionId);
        tcpClientChannelObjectMap.put(channel, session);
        afterLink(session);
        return session;
    }


    public final <T> void doTcpClientUnlink(Channel channel) {
        Session session = tcpClientChannelObjectMap.remove(channel);
        afterUnlink(session);
    }

    public Session getTcpClientSession(Channel channel) {
        return tcpClientChannelObjectMap.get(channel);
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
                Logs.DEFAULT_LOGGER.info("移除超时的channel" + channel);
            }
        }
    }

    private int incAndGetSessionId() {
        sessionIdInc++;
        return sessionIdInc;
    }


    public void broadcast(ByteMsgObj byteMsgObj){
        for(Channel channel : channelObjectMap.keySet()){
            channel.writeAndFlush(byteMsgObj);
        }
    }
 
    public void broadcast(MessageLite protoMessage){
        for(Channel channel : channelObjectMap.keySet()){
            channel.writeAndFlush(protoMessage);
        }
    }

    public List<Session> getSessions(){
        return new ArrayList<>(channelObjectMap.values());
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

        for(Session session : channelObjectMap.values()){
            session.getChannel().close();
        }

    }

    public String getSessionAddr(Session session){
        return AllUtil.getChannelRemoteAddr(session.getChannel());
    }

}
