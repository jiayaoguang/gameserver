package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.HttpRequestEvent;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.intercept.MsgInterceptor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public abstract class AbstractProcessor<T> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

//	private Context context;

    private GameConsumer gameConsumer;

    private MsgInterceptor<?> interceptor = null;

    public void setMsgInterceptor(MsgInterceptor msgInterceptor) {
        this.interceptor = msgInterceptor;
    }

    public boolean checkIntercepts(Session session, MsgEvent event) {

        if (interceptor !=null && !this.interceptor.intercept(session, event)) {
            return false;
        }

        return true;
    }


    public GameContext getContext() {
        return gameConsumer.getGameContext();
    }


    public abstract void process(Session session, MsgEvent<T> event);


    public GameConsumer getGameConsumer() {
        return gameConsumer;
    }

    public void setGameConsumer(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }


}

