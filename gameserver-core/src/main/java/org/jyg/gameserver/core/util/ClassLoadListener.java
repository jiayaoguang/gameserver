package org.jyg.gameserver.core.util;

import org.jyg.gameserver.core.consumer.GameConsumer;

/**
 * create by jiayaoguang at 2021/8/14
 */
public interface ClassLoadListener {

    void beforeLoadProcessor(GameConsumer gameConsumer);


    void afterLoad(GameConsumer gameConsumer);

}
