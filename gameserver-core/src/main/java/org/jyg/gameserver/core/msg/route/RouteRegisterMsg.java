package org.jyg.gameserver.core.msg.route;

import io.protostuff.Tag;
import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang on 2022/8/7
 */
public class RouteRegisterMsg implements ByteMsgObj {

    @Tag(1)
    private int serverId;

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }




}
