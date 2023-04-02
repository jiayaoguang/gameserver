package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.event.Event;

/**
 * create by jiayaoguang on 2022/10/29
 */
public interface GameEventListener<E extends Event> {


    public void onEvent(E e);

}
