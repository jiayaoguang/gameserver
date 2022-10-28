package org.jyg.gameserver.core.event;

/**
 * create by jiayaoguang on 2022/10/29
 */
public interface GameEventListener<E extends Event> {


    public void onEvent(E e);

}
