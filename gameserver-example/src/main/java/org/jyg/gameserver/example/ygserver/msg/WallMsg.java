package org.jyg.gameserver.example.ygserver.msg;

/**
 * create by jiayaoguang on 2021/9/5
 */
public class WallMsg {

    private Vector2Msg posi;
    private int width;
    private int height;


    public Vector2Msg getPosi() {
        return posi;
    }

    public void setPosi(Vector2Msg posi) {
        this.posi = posi;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
