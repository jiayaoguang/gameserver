package org.jyg.gameserver.route;

public class RouteConfig {
    /**
     * 远程server ip
     */
    private String remoteGameServerIp;
    /**
     * 远程server 接口
     */
    private int remoteGameServerPort;

    /**
     * 必选配置,默认 8081
     */
    private int routeTcpPort = 8081;

    /**
     * 可选配置 0/没配表示不开http端口
     */
    private int routeHttpPort;


    public String getRemoteGameServerIp() {
        return remoteGameServerIp;
    }

    public void setRemoteGameServerIp(String remoteGameServerIp) {
        this.remoteGameServerIp = remoteGameServerIp;
    }

    public int getRemoteGameServerPort() {
        return remoteGameServerPort;
    }

    public void setRemoteGameServerPort(int remoteGameServerPort) {
        this.remoteGameServerPort = remoteGameServerPort;
    }


    public int getRouteTcpPort() {
        return routeTcpPort;
    }

    public void setRouteTcpPort(int routeTcpPort) {
        this.routeTcpPort = routeTcpPort;
    }

    public int getRouteHttpPort() {
        return routeHttpPort;
    }

    public void setRouteHttpPort(int routeHttpPort) {
        this.routeHttpPort = routeHttpPort;
    }
}
