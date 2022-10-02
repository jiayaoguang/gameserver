package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.util.LruMap;
import org.jyg.gameserver.example.ygserver.msg.PlayerFrameMsg;

import java.util.Map;

/**
 * create by jiayaoguang on 2021/9/11
 */
@Deprecated
public class FrameManager {

    private Map<Long , PlayerFrameMsg> playerFrameMsgMap = new LruMap<>(100);


    public Map<Long, PlayerFrameMsg> getPlayerFrameMsgMap() {
        return playerFrameMsgMap;
    }

    public void setPlayerFrameMsgMap(Map<Long, PlayerFrameMsg> playerFrameMsgMap) {
        this.playerFrameMsgMap = playerFrameMsgMap;
    }



}
