package org.jyg.gameserver.example.ygserver.msg;

import org.jyg.gameserver.core.msg.ByteMsgObj;

import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2021/9/11
 */
public class ServerFrameMsg implements ByteMsgObj {

    private List<PlayerFrameMsg> playerFrameMsgs = new ArrayList<>();

    public List<PlayerFrameMsg> getPlayerFrameMsgs() {
        return playerFrameMsgs;
    }

    public void setPlayerFrameMsgs(List<PlayerFrameMsg> playerFrameMsgs) {
        this.playerFrameMsgs = playerFrameMsgs;
    }
}
