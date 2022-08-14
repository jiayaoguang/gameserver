package org.jyg.gameserver.route;

public class RouteConfig {

    private String gameServerIp;
    private int gameServerPort;


    public String getGameServerIp() {
        return gameServerIp;
    }

    public void setGameServerIp(String gameServerIp) {
        this.gameServerIp = gameServerIp;
    }

    public int getGameServerPort() {
        return gameServerPort;
    }

    public void setGameServerPort(int gameServerPort) {
        this.gameServerPort = gameServerPort;
    }
}
