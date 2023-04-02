package org.jyg.gameserver.core.session;

import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.route.RouteReplyMsg;
import org.jyg.gameserver.core.util.GameContext;

/**
 * create by jiayaoguang on 2022/8/7
 */
public class RouteSession extends Session {

    private final Session routeServerSession;
    private final GameContext gameContext;

    private String remoteAddr;
//    public RouteSession(GameContext gameContext, Session routeServerSession, long sessionId){
//        this(gameContext, routeServerSession , sessionId , null);
//    }
    public RouteSession(GameContext gameContext, Session routeServerSession, long sessionId, String remoteAddr) {
        super(sessionId);
        this.routeServerSession = routeServerSession;
        this.gameContext = gameContext;
        this.remoteAddr = remoteAddr;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    protected void writeObjMessage(Object msgObj) {
        RouteReplyMsg routeReplyMsg = new RouteReplyMsg();


        int msgId = gameContext.getMsgIdByMsgObj(msgObj);

        if(msgId <= 0){
            throw new IllegalArgumentException("unknow msg obj : " + msgObj);
        }
        AbstractMsgCodec msgCodec = gameContext.getMsgCodec(msgId);
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
