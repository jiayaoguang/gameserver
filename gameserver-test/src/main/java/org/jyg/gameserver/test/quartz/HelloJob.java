package org.jyg.gameserver.test.quartz;

import org.quartz.Job;

/**
 * create by jiayaoguang on 2022/11/27
 */
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob implements Job {
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        System.out.println("Hello Quartz!" + Thread.currentThread().getName() + " time : " + System.currentTimeMillis());
    }
}


