package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2024/10/26
 */
public class IgnoreResultHandler implements ResultHandler{

    public static final IgnoreResultHandler IGNORE_RESULT_HANDLER = new IgnoreResultHandler();

    public static IgnoreResultHandler getInstance(){
        return IGNORE_RESULT_HANDLER;
    }

    private IgnoreResultHandler(){

    }


    @Override
    public void call(int eventId, Object data) {
        Logs.DEFAULT_LOGGER.warn("event {} data {} resultHandler is ignore ",eventId, data);
    }

}
