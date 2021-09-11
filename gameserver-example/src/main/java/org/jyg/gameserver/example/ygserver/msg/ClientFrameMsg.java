package org.jyg.gameserver.example.ygserver.msg;

import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;

/**
 * create by jiayaoguang on 2021/9/11
 */
public class ClientFrameMsg implements ByteMsgObj {

    private long frameTime;
    private Vector2Msg posi;

    private Vector2Msg dir;

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

    public Vector2Msg getDir() {
        return dir;
    }

    public void setDir(Vector2Msg dir) {
        this.dir = dir;
    }
}
