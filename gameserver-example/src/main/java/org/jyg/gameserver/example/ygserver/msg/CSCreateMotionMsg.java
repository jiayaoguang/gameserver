package org.jyg.gameserver.example.ygserver.msg;

import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang on 2021/8/29
 */
public class CSCreateMotionMsg implements ByteMsgObj {

    private int type;

    private Vector2Msg posi;



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
}
