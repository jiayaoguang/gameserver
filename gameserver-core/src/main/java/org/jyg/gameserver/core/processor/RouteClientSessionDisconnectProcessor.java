package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.manager.RouteManager;
import org.jyg.gameserver.core.msg.route.RouteClientSessionDisconnectMsg;
import org.jyg.gameserver.core.session.Session;

public class RouteClientSessionDisconnectProcessor extends ByteMsgObjProcessor<RouteClientSessionDisconnectMsg> {

    @Override
    public void process(Session session, MsgEvent<RouteClientSessionDisconnectMsg> event) {

        Session routeClientSession = this.getGameConsumer().getInstance(RouteManager.class).getRouteSession(session.getSessionId() , event.getMsgData().getSessionId());
        if(routeClientSession != null){
            getGameConsumer().getChannelManager().afterDisconnect(routeClientSession);
        }


        RouteManager routeManager = this.getGameConsumer().getInstance(RouteManager.class);
        routeManager.removeRouteClientSession( session , event.getMsgData().getSessionId());

    }
}
