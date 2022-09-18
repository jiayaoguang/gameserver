package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.example.ygserver.msg.SCTipMsg;

/**
 * create by jiayaoguang at 2021/9/4
 */
public class Player {

    private PlayerDB playerDB;

    private long sessionId;

    private Session session;


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

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }


    public void sendTip(String content){
        SCTipMsg tipMsg = new SCTipMsg();
        tipMsg.setContent(content);

        session.writeMessage(tipMsg);
    }

}
