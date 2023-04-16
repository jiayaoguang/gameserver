package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.intercept.WhiteIpInterceptor;
import org.jyg.gameserver.core.manager.RouteManager;
import org.jyg.gameserver.core.msg.route.RouteRegisterMsg;
import org.jyg.gameserver.core.session.Session;

public class RouteRegisterMsgProcessor extends ByteMsgObjProcessor<RouteRegisterMsg>{

    public RouteRegisterMsgProcessor() {
        setMsgInterceptor(new WhiteIpInterceptor());
    }

    @Override
    public void process(Session session, MsgEvent<RouteRegisterMsg> event) {
        RouteManager routeManager = this.getGameConsumer().getInstance(RouteManager.class);
        Session routeSession = routeManager.getRouteSession(session.getSessionId() , session.getSessionId());

        if(routeSession == null){
            routeManager.addRouteInfo(event.getMsgData().getServerId(),session);
        }
    }


}
