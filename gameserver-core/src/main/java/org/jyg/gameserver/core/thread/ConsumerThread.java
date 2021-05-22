package org.jyg.gameserver.core.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * create by jiayaoguang at 2021/5/22
 */
public class ConsumerThread extends Thread{

    private final List<Runnable> runnableList = new CopyOnWriteArrayList<>();

    public ConsumerThread(){
        super();
    }

    public ConsumerThread(Runnable runnable){
        super(runnable);
        runnableList.add(runnable);
    }

    public void addRunnable(Runnable runnable){
        runnableList.add(runnable);
    }


    @Override
    public void run() {
        if(runnableList.size() <= 1){
            super.run();
            return;
        }

        for(Runnable runnable : runnableList ){
            runnable.run();
        }
    }
}
