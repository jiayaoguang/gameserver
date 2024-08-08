package org.jyg.gameserver.core.msg;

/**
 * create by jiayaoguang on 2024/8/8
 */
public class GetServerTimeResponseMsg  implements ByteMsgObj {

    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
