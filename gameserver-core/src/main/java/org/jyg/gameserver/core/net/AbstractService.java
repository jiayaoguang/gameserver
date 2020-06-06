package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.consumer.Consumer;

/**
 * create by jiayaoguang on 2020/5/2
 */
public abstract class AbstractService implements Service{

    protected final Consumer defaultConsumer;


    protected AbstractService(Consumer defaultConsumer) {
        this.defaultConsumer = defaultConsumer;
    }

    public Consumer getDefaultConsumer() {
        return defaultConsumer;
    }

}
