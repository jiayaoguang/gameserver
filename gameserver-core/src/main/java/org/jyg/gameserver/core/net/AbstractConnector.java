package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.util.Context;

/**
 * create by jiayaoguang on 2020/5/2
 */
public abstract class AbstractConnector implements Connector {

    protected final Consumer defaultConsumer;


    protected AbstractConnector(Consumer defaultConsumer) {
        this.defaultConsumer = defaultConsumer;
    }

    public Consumer getDefaultConsumer() {
        return defaultConsumer;
    }



    public Context getContext(){
        return defaultConsumer.getContext();
    }

}
