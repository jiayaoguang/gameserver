package org.jyg.gameserver.util.timer.cron;

import org.quartz.simpl.SimpleThreadPool;

/**
 * create by jiayaoguang on 2023/2/11
 */
public class QuartzSingleThreadPool extends SimpleThreadPool {


    public QuartzSingleThreadPool() {
        super(3, Thread.NORM_PRIORITY);
    }
}
