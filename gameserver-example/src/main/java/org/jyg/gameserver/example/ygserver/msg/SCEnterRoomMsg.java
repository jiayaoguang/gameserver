package org.jyg.gameserver.example.ygserver.msg;

import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.example.ygserver.msg.data.MotionMsg;

import java.util.ArrayList;
import java.util.List;

public class SCEnterRoomMsg implements ByteMsgObj {

    private List<PlayerInfoMsg> playerInfoMsgs = new ArrayList<>();

    public List<WallMsg> wallMsgs = new ArrayList<>();

    public List<MotionMsg> motionMsgs = new ArrayList<>();

    public int score = 0;

    public List<PlayerInfoMsg> getPlayerInfoMsgs() {
        return playerInfoMsgs;
    }

    public void setPlayerInfoMsgs(List<PlayerInfoMsg> playerInfoMsgs) {
        this.playerInfoMsgs = playerInfoMsgs;
    }

    public List<WallMsg> getWallMsgs() {
        return wallMsgs;
    }

    public void setWallMsgs(List<WallMsg> wallMsgs) {
        this.wallMsgs = wallMsgs;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<MotionMsg> getMotionMsgs() {
        return motionMsgs;
    }

    public void setMotionMsgs(List<MotionMsg> motionMsgs) {
        this.motionMsgs = motionMsgs;
    }
}
