package org.jyg.gameserver.example.ygserver.msg;

import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.example.ygserver.msg.data.MotionMsg;

/**
 * create by jiayaoguang on 2021/8/29
 */
public class SCCreateMotionMsg implements ByteMsgObj {

    private MotionMsg motionMsg;

    private int costScore;

    private int currentScore;


    public MotionMsg getMotionMsg() {
        return motionMsg;
    }

    public void setMotionMsg(MotionMsg motionMsg) {
        this.motionMsg = motionMsg;
    }


    public int getCostScore() {
        return costScore;
    }

    public void setCostScore(int costScore) {
        this.costScore = costScore;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }
}
