package org.jyg.gameserver.core.filter;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2021/7/31
 */
public interface MsgFilter <T>{

    public boolean filter(Session session , EventData<T> eventData);

}

