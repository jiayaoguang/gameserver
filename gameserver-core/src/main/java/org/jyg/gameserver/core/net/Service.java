package org.jyg.gameserver.core.net;

/**
 * Created by jiayaoguang on 2018/7/6.
 */
public interface Service {

    void start() throws InterruptedException;

    void stop();
}
