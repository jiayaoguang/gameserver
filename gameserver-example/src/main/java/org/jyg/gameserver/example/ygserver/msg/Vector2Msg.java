package org.jyg.gameserver.example.ygserver.msg;

/**
 * create by jiayaoguang on 2021/9/5
 */
public class Vector2Msg {
    private float x;

    private float y;

    public Vector2Msg() {
    }

    public Vector2Msg(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
