package org.jyg.gameserver.example.ygserver;

import io.netty.channel.Channel;
import org.jyg.gameserver.core.util.LruMap;

import java.util.HashMap;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/9/4
 */
public class PlayerManager {



    private Map<Long, Player> loginPlayerMap = new LruMap<>(100);


    public PlayerManager() {
    }


    public Map<Long, Player> getLoginPlayerMap() {
        return loginPlayerMap;
    }

    public void setLoginPlayerMap(Map<Long, Player> loginPlayerMap) {
        this.loginPlayerMap = loginPlayerMap;
    }
}
