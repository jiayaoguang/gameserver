package org.jyg.gameserver.example.ygserver.msg;

import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang on 2022/10/7
 */
public class CSEatScoreMotionMsg implements ByteMsgObj {

    private long motionUid;


    public long getMotionUid() {
        return motionUid;
    }

    public void setMotionUid(long motionUid) {
        this.motionUid = motionUid;
    }
}
