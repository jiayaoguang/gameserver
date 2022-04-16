package org.jyg.gameserver.core.manager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * create by jiayaoguang on 2021/5/22
 */
public class ScheduleManager implements Lifecycle {

    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);


    @Override
    public void start() {

    }

    public void schedule(Runnable command, long delayTime) {
        scheduledExecutorService.schedule(command, delayTime, TimeUnit.MILLISECONDS);
    }

    public void schedule(Runnable command, long initialDelay, long delay) {
        scheduledExecutorService.scheduleWithFixedDelay(command, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    public void scheduleAtFixedRate(Runnable command, long initialDelay, long period) {
        scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
    }


    @Override
    public void stop() {
        scheduledExecutorService.shutdown();
        try {
            if (!scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
                // 超时的时候向线程池中所有的线程发出中断(interrupted)。
                scheduledExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            scheduledExecutorService.shutdownNow();
        }
    }


    public static class GlobalTimer implements Runnable {

        private volatile boolean isCancel = false;

        private final Runnable command;

        public GlobalTimer(Runnable command) {
            this.command = command;
        }

        public void cancel(){
            isCancel = true;
        }

        @Override
        public void run() {
            if(isCancel){
                return;
            }
            command.run();

        }
    }

}
