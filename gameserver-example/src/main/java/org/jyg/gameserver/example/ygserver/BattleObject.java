package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.example.ygserver.msg.Vector2Msg;

/**
 * create by jiayaoguang on 2022/9/1
 */
public class BattleObject {

    public long id;




    private Vector2Msg posi;

    private float dir;

    private int maxHp;

    private int hp;

    private Vector2Msg scale;



    public Vector2Msg getPosi() {
        return posi;
    }

    public void setPosi(Vector2Msg posi) {
        this.posi = posi;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public float getDir() {
        return dir;
    }

    public void setDir(float dir) {
        this.dir = dir;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Vector2Msg getScale() {
        return scale;
    }

    public void setScale(Vector2Msg scale) {
        this.scale = scale;
    }
}
