package org.jyg.gameserver.util.timer.cron;

import org.jyg.gameserver.core.util.AllUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * create by jiayaoguang on 2023/2/11
 */
public class HelloJob implements Job {


    public HelloJob() {

        int i = 0;

    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String date = context.getJobDetail().getJobDataMap().getString("data");
        AllUtil.println("hello job :" + Thread.currentThread().getName() + ";"+date );
    }
}
