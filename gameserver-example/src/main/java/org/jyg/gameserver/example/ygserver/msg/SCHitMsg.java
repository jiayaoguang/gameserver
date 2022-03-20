package org.jyg.gameserver.example.ygserver.msg;

import org.jyg.gameserver.core.msg.ByteMsgObj;

public class SCHitMsg implements ByteMsgObj {

    private int score = 0;

    private int addScore = 0;

    private long hitTargetId = 0L;

    private int targetHp = 0;

    private long attackPlayerId = 0L;

    private int attackPlayerHp = 0;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getAddScore() {
        return addScore;
    }

    public void setAddScore(int addScore) {
        this.addScore = addScore;
    }


    public long getHitTargetId() {
        return hitTargetId;
    }

    public void setHitTargetId(long hitTargetId) {
        this.hitTargetId = hitTargetId;
    }


    public int getTargetHp() {
        return targetHp;
    }

    public void setTargetHp(int targetHp) {
        this.targetHp = targetHp;
    }

    public long getAttackPlayerId() {
        return attackPlayerId;
    }

    public void setAttackPlayerId(long attackPlayerId) {
        this.attackPlayerId = attackPlayerId;
    }

    public int getAttackPlayerHp() {
        return attackPlayerHp;
    }

    public void setAttackPlayerHp(int attackPlayerHp) {
        this.attackPlayerHp = attackPlayerHp;
    }
}
