package org.jyg.gameserver.example.ygserver;

/**
 * create by jiayaoguang at 2021/9/4
 */
public class Player {

    private PlayerDB playerDB;

    private long sessionId;


    public PlayerDB getPlayerDB() {
        return playerDB;
    }

    public void setPlayerDB(PlayerDB playerDB) {
        this.playerDB = playerDB;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
}
