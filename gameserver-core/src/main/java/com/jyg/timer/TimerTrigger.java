package com.jyg.timer;
/**
 * created by jiayaoguang at 2018年3月22日
 */

import java.util.PriorityQueue;

public class TimerTrigger {

    private final PriorityQueue<Timer> timerQueue = new PriorityQueue<>();

    public void addTimer(Timer timer) {
        timer.setStartTime(System.currentTimeMillis());
        timerQueue.add(timer);
    }

    public void tickTigger() {

        for (; ; ) {
            Timer timer = timerQueue.peek();
            if (timer == null) {
                return;
            }
            if (timer.getTriggerTime() > System.currentTimeMillis()) {
                return;
            }
            timerQueue.poll();
            if (timer.getExeNum() <= 0) {

                continue;
            }
            timer.setStartTime(System.currentTimeMillis());
            timer.setExeNum(timer.getExeNum() - 1);

            timer.trigger();

            timerQueue.offer(timer);

        }

    }
}
