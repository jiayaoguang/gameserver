package org.jyg.gameserver.core.session;

import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.route.RouteReplyMsg;
import org.jyg.gameserver.core.util.Context;

/**
 * create by jiayaoguang on 2022/8/7
 */
public class RouteSession extends Session {

    private final Session routeServerSession;
    private final Context context;

    private String remoteAddr;
    public RouteSession(Context context, Session routeServerSession, long sessionId){
        this(context , routeServerSession , sessionId , null);
    }
    public RouteSession(Context context, Session routeServerSession, long sessionId, String remoteAddr) {
        super(sessionId);
        this.routeServerSession = routeServerSession;
        this.context = context;
        this.remoteAddr = remoteAddr;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    protected void writeObjMessage(Object msgObj) {
        RouteReplyMsg routeReplyMsg = new RouteReplyMsg();


        int msgId = context.getMsgIdByMsgObj(msgObj);

        if(msgId <= 0){
            throw new IllegalArgumentException("unknow msg obj : " + msgObj);
        }
        AbstractMsgCodec msgCodec = context.getMsgCodec(msgId);
        try {
            routeReplyMsg.setData(msgCodec.encode(msgObj));
        } catch (Exception e) {
            e.printStackTrace();
        }
        routeReplyMsg.setMsgId(msgId);
        routeReplyMsg.setSessionId(getSessionId());

        this.routeServerSession.writeMessage(routeReplyMsg);
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
