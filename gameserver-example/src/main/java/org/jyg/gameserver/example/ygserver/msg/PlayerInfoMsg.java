package org.jyg.gameserver.example.ygserver.msg;

/**
 * create by jiayaoguang on 2021/9/11
 */
public class PlayerInfoMsg {

    private long playerId;
    private String name;
    private int hp;

    private Vector2Msg posi;

    private float dir;

    private int state = 0;

    private float playerSize = 0;


    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public Vector2Msg getPosi() {
        return posi;
    }

    public void setPosi(Vector2Msg posi) {
        this.posi = posi;
    }

    public float getDir() {
        return dir;
    }

    public void setDir(float dir) {
        this.dir = dir;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    public float getPlayerSize() {
        return playerSize;
    }

    public void setPlayerSize(float playerSize) {
        this.playerSize = playerSize;
    }
}
