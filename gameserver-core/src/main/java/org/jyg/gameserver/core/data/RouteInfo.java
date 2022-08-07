package org.jyg.gameserver.core.data;

import org.jyg.gameserver.core.session.Session;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * create by jiayaoguang on 2022/8/7
 */
public class RouteInfo {

    private final int serverId;

    private final Map<Long , Session> routeSessions = new LinkedHashMap<>();


    public RouteInfo(int serverId) {
        this.serverId = serverId;
    }

    public int getServerId() {
        return serverId;
    }


    public Map<Long, Session> getRouteSessions() {
        return routeSessions;
    }

}
