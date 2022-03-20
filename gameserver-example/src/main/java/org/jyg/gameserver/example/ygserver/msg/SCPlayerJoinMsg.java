package org.jyg.gameserver.example.ygserver.msg;

import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang on 2021/8/29
 */
public class SCPlayerJoinMsg implements ByteMsgObj {

    private PlayerInfoMsg playerInfoMsg;


    public PlayerInfoMsg getPlayerInfoMsg() {
        return playerInfoMsg;
    }

    public void setPlayerInfoMsg(PlayerInfoMsg playerInfoMsg) {
        this.playerInfoMsg = playerInfoMsg;
    }

}
