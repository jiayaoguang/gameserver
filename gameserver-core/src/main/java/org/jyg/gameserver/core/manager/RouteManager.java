package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.data.RouteInfo;
import org.jyg.gameserver.core.session.RouteSession;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Logs;

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

    public void addRouteClientSession(Session routeServerSession , RouteSession routeSession ){
        long sessionId = routeServerSession.getSessionId();
        RouteInfo routeInfo = routeInfoMap.get(sessionId);

        if(routeInfo == null){
//            throw new IllegalArgumentException("route not register");
            Logs.DEFAULT_LOGGER.error("addRouteClientSession fail , not found RouteInfo");
            return;
        }

        routeInfo.getRouteSessions().put(routeSession.getSessionId() , routeSession);
    }



    public RouteSession removeRouteClientSession(Session routeServerSession , long routeClientSessionId ){
        long sessionId = routeServerSession.getSessionId();
        RouteInfo routeInfo = routeInfoMap.get(sessionId);

        if(routeInfo == null){
//            throw new IllegalArgumentException("route not register");
            return null;
        }

       return routeInfo.getRouteSessions().remove(routeClientSessionId);
    }



    public void addRouteInfo(int serverId ,  Session routeServerSession){
        long sessionId = routeServerSession.getSessionId();
        RouteInfo routeInfo = routeInfoMap.get(sessionId);

        if(routeInfo == null){
//            throw new IllegalArgumentException("route not register");
            routeInfo = new RouteInfo(serverId , routeServerSession);
            routeInfoMap.put(sessionId , routeInfo);
        }
    }

    public void removeRouteInfo(int serverId ,  Session routeServerSession){
        long sessionId = routeServerSession.getSessionId();
        RouteInfo routeInfo = routeInfoMap.remove(sessionId);

        if(routeInfo == null){
//            throw new IllegalArgumentException("route not register");

        }
    }

}
