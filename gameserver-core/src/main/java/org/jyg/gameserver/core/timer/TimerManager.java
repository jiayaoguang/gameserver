package org.jyg.gameserver.core.timer;
/**
 * created by jiayaoguang at 2018年3月22日
 */

import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.core.util.Logs;

import java.util.PriorityQueue;

public class TimerManager implements Lifecycle {

    private final PriorityQueue<Timer> timerQueue = new PriorityQueue<>(128);


    public <T extends Timer> T addTimer(T timer) {
        timerQueue.add(timer);
        return timer;
    }

    public Timer addTimer(int execNum, long firstExecTime, long intervalTime, ITimerHandler timerHandler) {
        Timer timer = new DefaultTimer(execNum, firstExecTime, intervalTime, timerHandler);
        return addTimer(timer);
    }

    public Timer addTimer(int execNum, long delayTime, ITimerHandler timerHandler) {
        Timer timer = new DefaultTimer(execNum, delayTime, timerHandler);
        return addTimer(timer);
    }

    /**
     * 添加不限制次数的定时器
     * @param firstExecTime 第一次开始执行时间
     * @param intervalTime 执行时间间隔
     * @param timerHandler 执行方法
     * @return Timer
     */
    public Timer addUnlimitedTimer(long firstExecTime,long intervalTime, ITimerHandler timerHandler) {
        Timer timer = new DefaultTimer(-1, firstExecTime, intervalTime, timerHandler);

        return addTimer(timer);
    }


    /**
     * 添加不限制次数的定时器
     * @param firstExecTime 第一次开始执行时间
     * @param intervalTime 执行时间间隔
     * @param timerHandler 执行方法
     * @return Timer
     */
    public Timer addUnlimitedTimer(long firstExecTime,long intervalTime,boolean ignoreExecTime, ITimerHandler timerHandler) {
        Timer timer = new DefaultTimer(-1, firstExecTime, intervalTime, timerHandler);
        timer.setIgnoreExecTime(ignoreExecTime);
        return addTimer(timer);
    }


    /**
     * 添加不限制次数的定时器
     * @param intervalTime 执行时间间隔
     * @param timerHandler 执行方法
     * @return Timer
     */
    public Timer addUnlimitedTimer(long intervalTime, ITimerHandler timerHandler) {
        Timer timer = new DefaultTimer(-1,  intervalTime, timerHandler);

        return addTimer(timer);
    }

    public Timer addUnlimitedTimer(long intervalTime,boolean ignoreExecTime, ITimerHandler timerHandler) {
        Timer timer = new DefaultTimer(-1,  intervalTime, timerHandler);
        timer.setIgnoreExecTime(ignoreExecTime);
        return addTimer(timer);
    }

    public void cancelTimer(Timer timer) {
        timer.cancel();
    }

    public void updateTimer() {

        int onceExecuteNum = 0;
        final long thisLoopStartTime = System.currentTimeMillis();

        for (Timer timer; ; ) {
            timer = timerQueue.peek();
            if (timer == null) {
                return;
            }


            if (timer.getTriggerTime() > thisLoopStartTime) {
                return;
            }

            timerQueue.poll();

            if (timer.isEnd() || timer.isCancel()) {
                continue;
            }
            final long timerStartExecTime = System.currentTimeMillis();

            try {
                timer.onTime();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(timer.isIgnoreExecTime()){
                timer.setTriggerTime(System.currentTimeMillis() + timer.getIntervalTimeMills());
            }else {
                timer.setTriggerTime(timer.getTriggerTime() + timer.getIntervalTimeMills());
            }
            if (timer.getExecNum() != -1) {
                timer.reduceExecNum(1);
            }
            long costTime = System.currentTimeMillis() - timerStartExecTime;

            if(costTime > 10){
                Logs.DEFAULT_LOGGER.info(" timer exec cost more time  {} mills  {} queue size {} "  , costTime , timer.toString() , timerQueue.size());
            }

            if (!timer.isEnd()) {
                timerQueue.offer(timer);
            }


            onceExecuteNum++;

            if(onceExecuteNum > 1000){
                Logs.DEFAULT_LOGGER.warn(" once Execute timer more {} " , onceExecuteNum);
                break;
            }

        }

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
