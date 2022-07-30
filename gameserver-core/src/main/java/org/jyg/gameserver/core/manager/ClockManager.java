package org.jyg.gameserver.core.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClockManager implements Lifecycle{

    private volatile long now;


    private final boolean useSystemClock;

    private ScheduledExecutorService scheduler;

    public ClockManager() {
        this(false);
    }

    public ClockManager(boolean useSystemClock) {
        this.useSystemClock = useSystemClock;
        now = System.currentTimeMillis();
    }

    @Override
    public void start() {

        if (!useSystemClock) {

            scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "System_Clock_Thread");
                thread.setDaemon(true);
                return thread;
            });
            scheduler.scheduleAtFixedRate(this::updateTime, 1, 1, TimeUnit.MILLISECONDS);
        }

    }

    public void updateTime() {
        this.now = System.currentTimeMillis();
    }

    @Override
    public void stop() {
        if(scheduler != null){
            scheduler.shutdownNow();
        }
    }

    public long currentTimeMillis(){
        if(useSystemClock){
            return  System.currentTimeMillis();
        }else {
            return now;
        }
    }

}
