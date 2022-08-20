package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.manager.RouteManager;
import org.jyg.gameserver.core.msg.route.RouteClientSessionDisconnectMsg;
import org.jyg.gameserver.core.session.RouteSession;
import org.jyg.gameserver.core.session.Session;

public class RouteClientSessionDisconnectProcessor extends ByteMsgObjProcessor<RouteClientSessionDisconnectMsg> {

    @Override
    public void process(Session session, EventData<RouteClientSessionDisconnectMsg> event) {

        Session routeClientSession = this.getConsumer().getInstance(RouteManager.class).getRouteSession(session.getSessionId() , event.getData().getSessionId());
        if(routeClientSession != null){
            getConsumer().getChannelManager().afterDisconnect(routeClientSession);
        }


        RouteManager routeManager = this.getConsumer().getInstance(RouteManager.class);
        routeManager.removeRouteClientSession( session , event.getData().getSessionId());

    }
}
