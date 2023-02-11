package org.jyg.gameserver.util.timer.cron;



import org.junit.Test;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.ExecTimeUtil;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.util.Date;

/**
 * create by jiayaoguang on 2023/2/11
 */
public class CronTest {


    public static void main(String[] args) throws ParseException, SchedulerException {


        long now = System.currentTimeMillis();

        Date date = new Date(now);


        org.apache.logging.log4j.core.util.CronExpression logCron = new org.apache.logging.log4j.core.util.CronExpression("* 5 5/10 1/2 1,7 ?");

        org.quartz.CronExpression quartzCron = new org.quartz.CronExpression("10 * * * * ?");


        Date next2 = quartzCron.getNextValidTimeAfter(date);
        Date next3 = logCron.getNextValidTimeAfter(date);


        ExecTimeUtil.exec("quartzonce",()->{
            for(int i=0;i<200;i++){
                Date next = quartzCron.getNextValidTimeAfter(date);
            }
        });

        ExecTimeUtil.exec("quartz",()->{
            for(int i=0;i<100000;i++){
                Date next = quartzCron.getNextValidTimeAfter(date);
            }
        });
        ExecTimeUtil.exec("LOG",()->{
            for(int i=0;i<100000;i++){
                Date next = logCron.getNextValidTimeAfter(date);
            }
        });



//        //创建一个scheduler
//        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//        scheduler.getContext().put("skey", "svalue");
//
//        //创建一个Trigger
//        Trigger trigger = TriggerBuilder.newTrigger()
//                .withIdentity("trigger1", "group1")
//                .usingJobData("t1", "tv1")
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(3)
//                        .repeatForever()).build();
//        trigger.getJobDataMap().put("t2", "tv2");
//
//        //创建一个job
//        JobDetail job = JobBuilder.newJob(HelloJob.class)
//                .usingJobData("j1", "jv1")
//                .withIdentity("myjob", "mygroup").build();
//        job.getJobDataMap().put("j2", "jv2");
//
//        //注册trigger并启动scheduler
//        scheduler.scheduleJob(job,trigger);
//        scheduler.start();



    }



    // note this part of code run in osgi container
    @Test
    public void testQuartz() throws Exception {

        String s = " {\"pp\":300.0,\"pv\":359.26282,\"num\":0,\"pi\":641,\"sp\":360.0,\"ix\":0}";


//        System.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS,SingleThreadPool.class.getName());
        Object  o =System.getProperty("org.quartz.threadPool.threadCount");
        System.setProperty("org.quartz.threadPool.threadCount","3");

//        System.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS,SingleThreadPool.class.getName());

        StdSchedulerFactory sf = new StdSchedulerFactory();


        Scheduler sched = sf.getScheduler();
        sched.getListenerManager().addJobListener(new JobListener() {
            @Override
            public String getName() {
                return "null";
            }

            @Override
            public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
                AllUtil.println("jobToBeExecuted");
            }

            @Override
            public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
                AllUtil.println("jobExecutionVetoed");
            }

            @Override
            public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
                AllUtil.println("jobWasExecuted");
            }
        });
        for(int i=0;i<5;i++){

            JobDetail job = JobBuilder.newJob(HelloJob.class)
                    .withIdentity("job_"+i+"_"+"cron", "group1").build();
            job.getJobDataMap().put("data","1111-"+i);
            org.quartz.Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger"+i, "group1").withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(2).repeatForever()).startNow().build();
            sched.scheduleJob(job, trigger);
        }




        sched.start();
        Thread.sleep(1000000);
        sched.shutdown(true);
    }



    @Test
    public void testOnce() throws ParseException {



        org.quartz.CronExpression quartzCron = new org.quartz.CronExpression("10 * * * * ? 2022");
        AllUtil.println(quartzCron.getNextValidTimeAfter(new Date()));
    }

}
