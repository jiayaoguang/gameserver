package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.intercept.AllowAllMsgInterceptor;
import org.jyg.gameserver.core.intercept.MsgInterceptor;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.GameContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public abstract class AbstractProcessor<T> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

//	private Context context;

    private GameConsumer gameConsumer;

    private boolean enableAccess = true;

    private MsgInterceptor<?> interceptor = AllowAllMsgInterceptor.getInstance();


    public void setMsgInterceptor(MsgInterceptor msgInterceptor) {
        this.interceptor = msgInterceptor;
    }

    public MsgInterceptor<?> getInterceptor() {
        return interceptor;
    }

    public boolean checkAccess(Session session, MsgEvent event) {

        if (interceptor ==null) {
            return true;
        }


        return this.interceptor.checkAccess(session, event);
    }

    public boolean checkAccessHttp(Request request, Response response) {

        if (interceptor ==null) {
            return true;
        }


        return this.interceptor.checkAccessHttp(request, response);
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


    public void setEnableAccess(boolean enableAccess) {
        this.enableAccess = enableAccess;
    }

    public boolean isEnableAccess() {
        return enableAccess;
    }
}

