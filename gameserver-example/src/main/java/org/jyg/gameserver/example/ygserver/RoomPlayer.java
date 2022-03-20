package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.example.ygserver.msg.Vector2Msg;

public class RoomPlayer {

//    private int id;

    private Player player = null;


    private int score;

    private int hp = 100;

    private Vector2Msg posi;

    private Vector2Msg dir;


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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

    public Vector2Msg getDir() {
        return dir;
    }

    public void setDir(Vector2Msg dir) {
        this.dir = dir;
    }
}
