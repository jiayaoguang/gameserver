package org.jyg.gameserver.example.ygserver.msg;

import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang on 2021/8/29
 */
public class CreateEnemyMsg implements ByteMsgObj {

    private long uid;
    private String name;


    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
