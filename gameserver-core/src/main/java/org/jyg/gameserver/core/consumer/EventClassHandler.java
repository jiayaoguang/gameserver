package org.jyg.gameserver.core.consumer;

/**
 * create by jiayaoguang on 2021/5/15
 */
public interface EventClassHandler<C> {
    void handle(int eventId , Object obj);
}
