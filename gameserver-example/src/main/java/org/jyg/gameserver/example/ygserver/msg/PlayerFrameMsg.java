package org.jyg.gameserver.example.ygserver.msg;

/**
 * create by jiayaoguang on 2021/9/11
 */
public class PlayerFrameMsg {

    private long playerId;

    private long clientTime;

    private Vector2Msg posi;

    private Vector2Msg dir;


    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getClientTime() {
        return clientTime;
    }

    public void setClientTime(long clientTime) {
        this.clientTime = clientTime;
    }

    public Vector2Msg getPosi() {
        return posi;
    }

    public void setPosi(Vector2Msg posi) {
        this.posi = posi;
    }

    public Vector2Msg getDir() {
        return dir;
    }

    public void setDir(Vector2Msg dir) {
        this.dir = dir;
    }
}
