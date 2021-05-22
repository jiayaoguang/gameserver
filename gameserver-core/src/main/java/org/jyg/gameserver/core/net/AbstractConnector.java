package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.util.Context;

/**
 * create by jiayaoguang on 2020/5/2
 */
public abstract class AbstractConnector implements Connector {

    protected Context context;

    protected AbstractConnector(Context context) {
        this.context = context;
    }




    public Context getContext(){
        return context;
    }

}
