package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.manager.ConsumerManager;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.Logs;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PullMQConnector extends MQConnector {


    private final Thread pollThread;

    private static final AtomicInteger pollThreadInc = new AtomicInteger(1);


    public PullMQConnector(Context context , int mqPushConsumerId ) {
        super(context,mqPushConsumerId);


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


        for(;context.isStart();){

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
