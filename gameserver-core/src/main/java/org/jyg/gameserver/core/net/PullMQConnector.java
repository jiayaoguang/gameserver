package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class PullMQConnector extends MQConnector {


    private final Thread pollThread;

    private static final AtomicInteger pollThreadInc = new AtomicInteger(1);


    public PullMQConnector(GameContext gameContext, int mqPushConsumerId ) {
        super(gameContext,mqPushConsumerId);


        this.pollThread = new Thread(this::run);
        pollThread.setName("KAFKA_POLL_THREAD_" + pollThreadInc.getAndIncrement());
    }


    @Override
    public void stop() {

        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(pollThread.isAlive()){
            Logs.DEFAULT_LOGGER.error("mq pollThread is alive");
        }else {
            Logs.DEFAULT_LOGGER.error("mq pollThread is stop");
        }

    }


    @Override
    public void start() {


        pollThread.setDaemon(false);
        pollThread.start();
    }


    private void run(){


        int pollEmptyCount = 0;


        for(; gameContext.isStart();){

            int pollMsgNum = pollMQMsg();

            if(pollMsgNum > 0){
                pollEmptyCount ++;

                if(pollEmptyCount >= 100){
                    pollEmptyCount = 0;
                    try {
                        Thread.sleep(2L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    }


    protected abstract int pollMQMsg();




}
