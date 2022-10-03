package org.jyg.gameserver.example.ygserver.msg;

import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;

/**
 * create by jiayaoguang on 2021/9/11
 */
public class ClientFrameMsg implements ByteMsgObj {

    private long frameTime;
    private Vector2Msg posi;

    private float dir;

    public Vector2Msg bulletPosi;
    public boolean bulletActive;

    public long getFrameTime() {
        return frameTime;
    }

    public void setFrameTime(long frameTime) {
        this.frameTime = frameTime;
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
}
