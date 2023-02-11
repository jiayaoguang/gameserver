package org.jyg.gameserver.util.timer.cron;

/**
 * create by jiayaoguang on 2023/2/11
 */
public class MyCronTask {

    private int fromConsumerId;

    private String cron;


    private Runnable runnable;


    public MyCronTask(int fromConsumerId, String cron, Runnable runnable) {
        this.fromConsumerId = fromConsumerId;
        this.cron = cron;
        this.runnable = runnable;
    }

    public int getFromConsumerId() {
        return fromConsumerId;
    }

    public String getCron() {
        return cron;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
