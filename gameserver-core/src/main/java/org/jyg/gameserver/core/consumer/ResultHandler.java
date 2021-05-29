package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang at 2021/5/22
 */
public interface ResultHandler<T> {

    void call(int eventId , T data);

    default void onTimeout(){
        Logs.DEFAULT_LOGGER.error(" {} timeout" , getClass().getName());
    }

}
