package org.jyg.gameserver.core.timer;
/**
 * created by jiayaoguang at 2018年3月22日
 */

import java.util.PriorityQueue;

public class TimerManager {

    private final PriorityQueue<Timer> timerQueue = new PriorityQueue<>(128);

    public Timer addTimer(Timer timer) {
        timerQueue.add(timer);
        return timer;
    }

    public Timer addTimer(int execNum, long firstDelayTime, long delayTime, ITimerHandler timerHandler) {
        Timer timer = new DefaultTimer(execNum, firstDelayTime, delayTime, timerHandler);
        return addTimer(timer);
    }

    public Timer addTimer(int execNum, long delayTime, ITimerHandler timerHandler) {
        Timer timer = new DefaultTimer(execNum, delayTime, delayTime, timerHandler);
        return addTimer(timer);
    }

    public void cancelTimer(Timer timer) {
        timer.cancel();
    }

    public void updateTimer() {

        for (Timer timer; ; ) {
            timer = timerQueue.peek();
            if (timer == null) {
                return;
            }
            if (timer.getTriggerTime() > System.currentTimeMillis()) {
                return;
            }

            timerQueue.poll();

            if (timer.isEnd() || timer.isCancel()) {
                continue;
            }

            timer.trigger();

            if (!timer.isEnd()) {
                timerQueue.offer(timer);
            }
        }

    }
}
