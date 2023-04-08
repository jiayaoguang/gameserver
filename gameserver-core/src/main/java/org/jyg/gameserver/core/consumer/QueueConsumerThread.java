package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.util.Logs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2023/4/8
 */
public class QueueConsumerThread extends Thread {

    private volatile boolean start = false;


    private final List<AbstractQueueGameConsumer> queueGameConsumers = new ArrayList<>();


    public QueueConsumerThread() {
        this(null);
    }


    public QueueConsumerThread(AbstractQueueGameConsumer firstQueueGameConsumer) {
        this.setDaemon(false);
        if (firstQueueGameConsumer != null) {
            addQueueConsumer(firstQueueGameConsumer);
        }
    }

    @Override
    public synchronized void start() {
        if (this.start) {
            return;
        }
        super.start();
        this.start = true;
    }


    @Override
    public void run() {

        Logs.DEFAULT_LOGGER.info("consumer thread {} start run .... " , getName());

        if (queueGameConsumers.isEmpty()) {
            throw new IllegalArgumentException("queueGameConsumers.isEmpty()");
        }

        for (GameConsumer gameConsumer : queueGameConsumers) {
            while (!gameConsumer.isStart()){
                LockSupport.parkNanos(1000 * 1000L);
            }
        }


        for (GameConsumer gameConsumer : queueGameConsumers) {
            gameConsumer.onThreadStart();
        }


        if (queueGameConsumers.size() == 1) {

            runSingleConsumer(queueGameConsumers.get(0));

        } else {

            runAllConsumers();

        }

        Logs.DEFAULT_LOGGER.info("queue consumer thread {} stop.... ",getName());
    }


    private void runSingleConsumer(AbstractQueueGameConsumer gameConsumer) {

        for (; ; ) {

            if (gameConsumer.isStop()) {
                break;
            }

            try {
                gameConsumer.updateConsumer();

            } catch (Exception e) {
                e.printStackTrace();
            }


            if (gameConsumer.getContinuePollNullNum() > 1000) {
                gameConsumer.update();
                gameConsumer.clearContinuePollNullNum();
                LockSupport.parkNanos(1000 * 1000L);
            } else if (gameConsumer.getContinuePollNullNum() > 800) {
                Thread.yield();
            }
        }
    }


    private void runAllConsumers() {
        for (; ; ) {
            int aliveConsumerNum = 0;

            int allContinuePollNullCountNum = 0;

            for (AbstractQueueGameConsumer gameConsumer : queueGameConsumers) {
                if (gameConsumer.isStop()) {
                    continue;
                } else {
                    aliveConsumerNum++;
                }
                try {
                    gameConsumer.updateConsumer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                allContinuePollNullCountNum += gameConsumer.getContinuePollNullNum();

            }

            if (allContinuePollNullCountNum > 1000) {
                for (AbstractQueueGameConsumer gameConsumer : queueGameConsumers) {
                    gameConsumer.update();
                    gameConsumer.clearContinuePollNullNum();
                }
                LockSupport.parkNanos(1000 * 1000L);
            } else if (allContinuePollNullCountNum > 800) {
                Thread.yield();
            }


            if (aliveConsumerNum == 0) {
                break;
            }
        }
    }


    public void addQueueConsumer(AbstractQueueGameConsumer queueGameConsumer) {
        queueGameConsumer.setConsumerThread(this);
        if(!queueGameConsumers.contains(queueGameConsumer)){
            queueGameConsumers.add(queueGameConsumer);
        }
    }

    public boolean isStart() {
        return start;
    }
}
