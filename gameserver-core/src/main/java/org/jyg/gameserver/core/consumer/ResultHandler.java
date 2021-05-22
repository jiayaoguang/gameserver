package org.jyg.gameserver.core.consumer;

/**
 * create by jiayaoguang at 2021/5/22
 */
public interface ResultHandler {

    void call(int eventId , Object data);

    void onTimeout();

}
