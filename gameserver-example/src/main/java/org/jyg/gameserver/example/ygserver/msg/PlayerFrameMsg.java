package org.jyg.gameserver.example.ygserver.msg;

/**
 * create by jiayaoguang on 2021/9/11
 */
public class PlayerFrameMsg {

    private long playerId;

    private long clientTime;

    private Vector2Msg posi;

    private Vector2Msg dir;

    private Vector2Msg bulletPosi;
    private boolean bulletActive;

    private String name;


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


    public Vector2Msg getBulletPosi() {
        return bulletPosi;
    }

    public void setBulletPosi(Vector2Msg bulletPosi) {
        this.bulletPosi = bulletPosi;
    }

    public boolean isBulletActive() {
        return bulletActive;
    }

    public void setBulletActive(boolean bulletActive) {
        this.bulletActive = bulletActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
