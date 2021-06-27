package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.util.IdUtil;

/**
 * create by jiayaoguang at 2021/6/9
 */
public class UidManager implements Lifecycle{
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }


    public long nextUid(){
        return IdUtil.nextId();
    }

}
