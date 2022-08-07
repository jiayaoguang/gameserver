package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.data.RouteInfo;
import org.jyg.gameserver.core.session.Session;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * create by jiayaoguang on 2022/8/7
 */
public class RouteManager implements Lifecycle {

    private final Map<Long , RouteInfo> routeInfoMap = new LinkedHashMap<>();


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }


    public Map<Long, RouteInfo> getRouteInfoMap() {
        return routeInfoMap;
    }



    public Session getRouteSession(long sessionId , long routeSessionId ){
        RouteInfo routeInfo = routeInfoMap.get(sessionId);

        if(routeInfo == null){
            return null;
        }

        return routeInfo.getRouteSessions().get(routeSessionId);
    }
    public void addRouteSession(int serverId , long sessionId , Session routeSession ){
        RouteInfo routeInfo = routeInfoMap.get(sessionId);

        if(routeInfo == null){
//            throw new IllegalArgumentException("route not register");
            routeInfo = new RouteInfo(serverId);
            routeInfoMap.put(sessionId , routeInfo);
        }

        routeInfo.getRouteSessions().put(routeSession.getSessionId() , routeSession);
    }



}
