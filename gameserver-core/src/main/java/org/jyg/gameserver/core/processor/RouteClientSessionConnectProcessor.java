package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.manager.RouteManager;
import org.jyg.gameserver.core.msg.route.RouteClientSessionConnectMsg;
import org.jyg.gameserver.core.session.RouteSession;
import org.jyg.gameserver.core.session.Session;

public class RouteClientSessionConnectProcessor extends ByteMsgObjProcessor<RouteClientSessionConnectMsg> {

    @Override
    public void process(Session session, EventData<RouteClientSessionConnectMsg> event) {

        RouteManager routeManager = this.getGameConsumer().getInstance(RouteManager.class);

        RouteSession routeClientSession = new RouteSession(getContext() , session , event.getData().getSessionId(), event.getData().getAddr());

        routeManager.addRouteClientSession( session , routeClientSession);


        getGameConsumer().getChannelManager().afterConnect(routeClientSession);

    }
}
