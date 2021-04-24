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

}
