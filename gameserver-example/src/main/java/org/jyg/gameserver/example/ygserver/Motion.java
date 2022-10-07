package org.jyg.gameserver.example.ygserver;

/**
 * create by jiayaoguang on 2022/9/1
 */
public class Motion extends BattleObject{

    private int type;

    private long createTime;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
