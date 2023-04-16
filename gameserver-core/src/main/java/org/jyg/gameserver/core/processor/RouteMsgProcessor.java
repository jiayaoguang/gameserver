package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.event.InnerMsgEvent;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.intercept.WhiteIpInterceptor;
import org.jyg.gameserver.core.manager.RouteManager;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.route.RouteMsg;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2022/8/7
 */
public class RouteMsgProcessor extends ByteMsgObjProcessor<RouteMsg> {
    public RouteMsgProcessor() {
        setMsgInterceptor(new WhiteIpInterceptor());
    }

    @Override
    public void process(Session session, MsgEvent<RouteMsg> event) {


        Session routeSession = this.getGameConsumer().getInstance(RouteManager.class).getRouteSession(session.getSessionId() , event.getMsgData().getSessionId());

        int msgId = event.getMsgData().getMsgId();

        AbstractMsgCodec msgCodec = getContext().getMsgCodec(msgId);
        try {
            Object msgObj =  msgCodec.decode(event.getMsgData().getData());


            InnerMsgEvent innerMsgEvent = new InnerMsgEvent(msgId , msgObj);


            getGameConsumer().processEventMsg( routeSession , innerMsgEvent );
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }
}
