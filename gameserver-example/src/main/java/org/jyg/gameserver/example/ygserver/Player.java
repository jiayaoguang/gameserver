package org.jyg.gameserver.example.ygserver;

/**
 * create by jiayaoguang at 2021/9/4
 */
public class Player {

    private PlayerDB playerDB;

    private int sessionId;


    public PlayerDB getPlayerDB() {
        return playerDB;
    }

    public void setPlayerDB(PlayerDB playerDB) {
        this.playerDB = playerDB;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}
