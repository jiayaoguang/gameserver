package org.jyg.gameserver.core.net;

import io.netty.channel.epoll.Epoll;
import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.RemotingUtil;

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



    public Context getContext(){
        return defaultConsumer.getContext();
    }

}
