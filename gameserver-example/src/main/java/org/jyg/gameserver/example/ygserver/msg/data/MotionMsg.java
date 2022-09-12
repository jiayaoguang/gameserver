package org.jyg.gameserver.example.ygserver.msg.data;

import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.example.ygserver.msg.Vector2Msg;

/**
 * create by jiayaoguang on 2021/8/29
 */
public class MotionMsg implements ByteMsgObj {


    private long ownPlayerId;

    private int type;

    private Vector2Msg posi;

    private long uid;


    private int maxHp;

    private int hp;

    private Vector2Msg scale;


    private Vector2Msg bulletPosi;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public Vector2Msg getPosi() {
        return posi;
    }

    public void setPosi(Vector2Msg posi) {
        this.posi = posi;
    }


    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public long getOwnPlayerId() {
        return ownPlayerId;
    }

    public void setOwnPlayerId(long ownPlayerId) {
        this.ownPlayerId = ownPlayerId;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }


    public Vector2Msg getBulletPosi() {
        return bulletPosi;
    }

    public void setBulletPosi(Vector2Msg bulletPosi) {
        this.bulletPosi = bulletPosi;
    }

    public Vector2Msg getScale() {
        return scale;
    }

    public void setScale(Vector2Msg scale) {
        this.scale = scale;
    }
}
