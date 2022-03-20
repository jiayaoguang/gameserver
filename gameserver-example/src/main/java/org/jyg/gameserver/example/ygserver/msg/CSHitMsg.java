package org.jyg.gameserver.example.ygserver.msg;

import org.jyg.gameserver.core.msg.ByteMsgObj;

public class CSHitMsg implements ByteMsgObj {


    private long hitTargetId = 0;

    public long getHitTargetId() {
        return hitTargetId;
    }

    public void setHitTargetId(long hitTargetId) {
        this.hitTargetId = hitTargetId;
    }
}
