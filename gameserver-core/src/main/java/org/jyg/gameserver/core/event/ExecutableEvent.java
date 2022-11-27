package org.jyg.gameserver.core.event;

public class ExecutableEvent implements Event {


    private final Runnable runnable;

    public ExecutableEvent(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
