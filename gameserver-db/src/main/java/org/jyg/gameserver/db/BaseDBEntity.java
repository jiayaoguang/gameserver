package org.jyg.gameserver.db;

import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.db.anno.DBTableFieldIgnore;

/**
 * create by jiayaoguang at 2021/5/22
 * db 实体对象需要继承这个类
 */
public abstract class BaseDBEntity implements Cloneable {

//    private long updateTime;
//    private long createTime;

    @DBTableFieldIgnore
    private long nextUpdateTime;


//    public long getUpdateTime() {
//        return updateTime;
//    }
//
//    public void setUpdateTime(long updateTime) {
//        this.updateTime = updateTime;
//    }
//
//    public long getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(long createTime) {
//        this.createTime = createTime;
//    }


    public long getNextUpdateTime() {
        return nextUpdateTime;
    }

    public void setNextUpdateTime(long nextUpdateTime) {
        this.nextUpdateTime = nextUpdateTime;
    }

    /**
     * 有自定义对象字段时需要重写改方法
     * @return a clone of this instance
     */
    @Override
    public Object clone(){
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            Logs.DEFAULT_LOGGER.error("make exception : " ,e);
        }
        return null;
    }

}
