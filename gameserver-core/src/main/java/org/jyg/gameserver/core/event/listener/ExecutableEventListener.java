package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.event.ExecutableEvent;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2022/11/26
 */
public class ExecutableEventListener implements GameEventListener<ExecutableEvent> {
    @Override
    public void onEvent(ExecutableEvent executableEvent) {
        try {
            executableEvent.getRunnable().run();
        }catch (Exception e){
            Logs.DEFAULT_LOGGER.error("make exception : " ,e);
        }
    }
}
