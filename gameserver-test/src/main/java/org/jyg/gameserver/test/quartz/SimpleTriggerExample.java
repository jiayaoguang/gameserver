package org.jyg.gameserver.test.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class SimpleTriggerExample {
    public static void main(String[] args) throws Exception {
        // Quartz 1.6.3
        // JobDetail job = new JobDetail();
        // job.setName("dummyJobName");
        // job.setJobClass(HelloJob.class);
        JobDetail job = JobBuilder.newJob(HelloJob.class)
                .withIdentity("dummyJobName", "group1").build();
        //Quartz 1.6.3
        // SimpleTrigger trigger = new SimpleTrigger();
        // trigger.setStartTime(new Date(System.currentTimeMillis() + 1000));
        // trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        // trigger.setRepeatInterval(30000);
        // Trigger the job to run on the next round minute
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("dummyTriggerName", "group1")
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(5).repeatForever())
                .build();
        // schedule it
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);

        CronExpression cronExpression = new CronExpression("0 0 2 1 * ? * ");
        Date t = cronExpression.getNextValidTimeAfter(new Date());
        System.out.println(t.getTime());
    }
}

