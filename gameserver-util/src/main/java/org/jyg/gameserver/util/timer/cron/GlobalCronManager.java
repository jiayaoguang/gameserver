package org.jyg.gameserver.util.timer.cron;

import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.core.util.GameContext;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2023/2/11
 */
public class GlobalCronManager implements Lifecycle {


    private Scheduler sched;


    private GameContext gameContext;

    private List<MyCronTask> cronTasks = new ArrayList<>();

    public GlobalCronManager(GameContext gameContext) {
        this.gameContext = gameContext;
    }


    public synchronized void addCronTask(MyCronTask cronTask){
        if(gameContext.isStart()){
            throw new IllegalStateException("already start");
        }
        cronTasks.add(cronTask);
    }


    @Override
    public void start() {


//        System.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS,SingleThreadPool.class.getName());
        if(System.getProperty("org.quartz.threadPool.threadCount") == null){
            //quzrtz 线程数量没配就设置为3个
            System.setProperty("org.quartz.threadPool.threadCount", "3");
        }

//        System.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS,SingleThreadPool.class.getName());

        StdSchedulerFactory sf = new StdSchedulerFactory();

        try {
            sched = sf.getScheduler();

            for( int i=0;i<cronTasks.size();i++ ){
                MyCronTask cronTask = cronTasks.get(i);
                JobDetail job = JobBuilder.newJob(MyCronJob.class)
                        .withIdentity("job_"+i+"_"+cronTask.getCron(), "group1").build();

                job.getJobDataMap().put("cronTask",cronTask);
                job.getJobDataMap().put("gameContext",gameContext);

                org.quartz.Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity("trigger_"+i+"_"+cronTask.getCron(), "group1").withSchedule(CronScheduleBuilder.cronSchedule(cronTask.getCron())
                        ).startNow().build();
                sched.scheduleJob(job, trigger);
            }


            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void stop() {
        try {
            sched.shutdown(true);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
