package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.manager.Lifecycle;

/**
 * Created by jiayaoguang on 2018/7/6.
 */
public interface Connector extends Lifecycle {

    void start();

    void stop();
}
