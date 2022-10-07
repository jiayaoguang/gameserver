package org.jyg.gameserver.example.ygserver.msg;

import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang on 2022/10/3
 */
public class SCUpdatePlayerScoreMsg implements ByteMsgObj {

    private long playerId;

    private int score;
    private float playerSize;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public float getPlayerSize() {
        return playerSize;
    }

    public void setPlayerSize(float playerSize) {
        this.playerSize = playerSize;
    }
}
