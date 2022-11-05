package org.jyg.gameserver.core.util;

/**
 * create by jiayaoguang on 2020/5/31
 */
public class ExecTimeUtil {

    private ExecTimeUtil() {
    }

    public static void exec(Runnable runnable){
        exec("" , runnable);
    }

    public static void exec(String operatorName , Runnable runnable){
        long startTime = System.nanoTime();
        runnable.run();
        System.out.println(operatorName + " exec cost : " + (System.nanoTime() - startTime)/(1000L*1000L));
    }

    public static void asynExec(String operatorName , Runnable runnable){
        ExecTimeThread execTimeThread = new ExecTimeThread(operatorName , runnable);
        execTimeThread.start();
    }


    public static class ExecTimeThread extends Thread{


        private final String operatorName;
        private final Runnable runnable;

        public ExecTimeThread(String operatorName, Runnable runnable) {
            this.operatorName = operatorName;
            this.runnable = runnable;
        }


        @Override
        public void run(){
            long startTime = System.nanoTime();
            runnable.run();
            System.out.println(operatorName + " exec cost : " + (System.nanoTime() - startTime)/(1000L*1000L));
        }

    }

}
