package org.jyg.gameserver.core.event;

/**
 * create by jiayaoguang on 2022/11/26
 */
public class ExecutableEventListener implements GameEventListener<ExecutableEvent>{
    @Override
    public void onEvent(ExecutableEvent executableEvent) {
        try {
            executableEvent.getRunnable().run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
