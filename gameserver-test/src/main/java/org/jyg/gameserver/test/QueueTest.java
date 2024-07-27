package org.jyg.gameserver.test;

import io.netty.util.internal.shaded.org.jctools.queues.MpscUnboundedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscUnboundedAtomicArrayQueue;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * create by jiayaoguang on 2020/5/31
 */
public class QueueTest {

    public static void main(String[] args) throws Exception {

        int threadNum = 1;


        final ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threadNum; i++){
            startQueueOffer("concurrentLinkedQueue offer" , concurrentLinkedQueue);
        }

        Thread.sleep(500);

        startQueuePoll("ConcurrentLinkedQueue poll" , concurrentLinkedQueue);

        Thread.sleep(500);

        final ArrayBlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(8000000);

        for (int i = 0; i < threadNum; i++){
            startQueueOffer("ArrayBlockingQueue offer" , blockingQueue);
        }

        Thread.sleep(500);
        startQueuePoll("ArrayBlockingQueue poll" , blockingQueue);
        Thread.sleep(500);


        final MpscUnboundedArrayQueue<String> mpscUnboundedArrayQueue = new MpscUnboundedArrayQueue<>(1024*1024);
        for (int i = 0; i < threadNum; i++){
            startQueueOffer("MpscUnboundedArrayQueue offer",mpscUnboundedArrayQueue);
        }

        Thread.sleep(500);

        startQueuePoll("MpscUnboundedArrayQueue poll" , mpscUnboundedArrayQueue);

        Thread.sleep(500);



        final LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>(8000000);
        for (int i = 0; i < threadNum; i++){
            startQueueOffer("LinkedBlockingQueue offer",linkedBlockingQueue);
        }

        Thread.sleep(500);

        startQueuePoll("LinkedBlockingQueue poll" , linkedBlockingQueue);

        Thread.sleep(500);


        final MpscUnboundedAtomicArrayQueue<String> mpscUnboundedAtomicArrayQueue = new MpscUnboundedAtomicArrayQueue<>(8000000);
        for (int i = 0; i < threadNum; i++){
            startQueueOffer("mpscUnboundedAtomicArrayQueue offer",mpscUnboundedAtomicArrayQueue);
        }

        Thread.sleep(500);

        startQueuePoll("mpscUnboundedAtomicArrayQueue poll" , mpscUnboundedAtomicArrayQueue);

        Thread.sleep(500);

    }

    public static void startQueueOffer(String name , Queue<String> queue){
        new Thread(() -> {
            exec(name, () -> {
                for (int i = 0; i < 10000000; i++) {
                    queue.offer("");
                }
            });
        }).start();
    }

    public static void startQueuePoll(String name , Queue<String> queue){
        new Thread(() -> {
            exec(name, () -> {
                for (;!queue.isEmpty();) {
                    queue.poll();
                }
            });
        }).start();
    }


    public static void exec(Runnable runnable){
        exec("" , runnable);
    }

    public static void exec(String operatorName , Runnable runnable){
        long startTime = System.currentTimeMillis();
        runnable.run();
        System.out.println(operatorName + " exec cost : " + (System.currentTimeMillis() - startTime));
    }

}
