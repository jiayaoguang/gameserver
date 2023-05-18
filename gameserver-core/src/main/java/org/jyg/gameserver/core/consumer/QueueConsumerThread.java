package org.jyg.gameserver.core.consumer;

import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.util.Logs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2023/4/8
 */
public abstract class QueueConsumerThread extends Thread {

    private volatile boolean start = false;


    protected final List<AbstractThreadQueueGameConsumer> queueGameConsumers = new ArrayList<>();


    public QueueConsumerThread() {
        this(null);
    }


    public QueueConsumerThread(String name) {
        this.setDaemon(false);
        if(StringUtils.isEmpty(name)){
            this.setName("QueueConsumerThread_"+getId());
        }else {
            this.setName(name);
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


    protected void runSingleConsumer(AbstractThreadQueueGameConsumer gameConsumer) {



        for (; ; ) {

            if (gameConsumer.isStop()) {
                break;
            }

            beforeUpdate();

            try {
                gameConsumer.update();
            } catch (Exception e) {
                e.printStackTrace();
            }


            afterUpdateApplyWait();
        }
    }



    protected void runAllConsumers() {


        for (; ; ) {
            int aliveConsumerNum = 0;

            beforeUpdate();

            for (AbstractThreadQueueGameConsumer gameConsumer : queueGameConsumers) {
                if (gameConsumer.isStop()) {
                    continue;
                } else {
                    aliveConsumerNum++;
                }
                try {
                    gameConsumer.update();
                } catch (Exception e) {
                    Logs.CONSUMER.error("update make exception : ",e);
                }

            }


            if (aliveConsumerNum == 0) {
                break;
            }

            afterUpdateApplyWait();

        }
    }


    protected abstract void beforeUpdate();
    protected abstract void afterUpdateApplyWait();

    void addQueueConsumer(AbstractThreadQueueGameConsumer queueGameConsumer) {

        if(!queueGameConsumers.contains(queueGameConsumer)){
            queueGameConsumers.add(queueGameConsumer);
        }
    }

    protected List<AbstractThreadQueueGameConsumer> getQueueGameConsumers() {
        return queueGameConsumers;
    }


    public boolean isStart() {
        return start;
    }
}
