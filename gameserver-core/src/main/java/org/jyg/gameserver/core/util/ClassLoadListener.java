package org.jyg.gameserver.core.util;

import org.jyg.gameserver.core.consumer.Consumer;

/**
 * create by jiayaoguang at 2021/8/14
 */
public interface ClassLoadListener {

    void beforeLoadProcessor(Consumer consumer);


    void afterLoad(Consumer consumer);

}
