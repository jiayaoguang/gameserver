package org.jyg.gameserver.core.data;

import org.jyg.gameserver.core.session.RouteSession;
import org.jyg.gameserver.core.session.Session;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * create by jiayaoguang on 2022/8/7
 */
public class RouteInfo {

    private final int serverId;

    private Session routeServerSession;

    private final Map<Long , RouteSession> routeSessions = new LinkedHashMap<>();


    public RouteInfo(int serverId , Session routeServerSession) {
        this.serverId = serverId;
        this.routeServerSession = routeServerSession;
    }

    public int getServerId() {
        return serverId;
    }


    public Map<Long, RouteSession> getRouteSessions() {
        return routeSessions;
    }

}
