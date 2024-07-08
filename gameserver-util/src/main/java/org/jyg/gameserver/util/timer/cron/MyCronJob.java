package org.jyg.gameserver.util.timer.cron;

import org.jyg.gameserver.core.event.ExecutableEvent;
import org.jyg.gameserver.core.util.GameContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * create by jiayaoguang on 2023/2/11
 */
public class MyCronJob  implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        MyCronTask myCronTask = (MyCronTask)context.getJobDetail().getJobDataMap().get("cronTask");
        GameContext gameContext = (GameContext)context.getJobDetail().getJobDataMap().get("gameContext");
        gameContext.getConsumerManager().publishcEvent(myCronTask.getFromConsumerId(), new ExecutableEvent(myCronTask.getRunnable()));
    }
}
