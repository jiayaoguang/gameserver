package org.jyg.gameserver.test;

import org.jyg.gameserver.core.util.AllUtil;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Auther: jiayaoguang
 * @Date: 2020/3/27
 * @Description: none
 */
public class JygTest {

    public static class A{

        public void run(){

        }

        @Override
        public void finalize(){
            ReentrantLock reentrantLock = new ReentrantLock();
            reentrantLock.lock();

        }

    }

    A a;

    public static void main(String[] args){
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(1);
        priorityQueue.add(10);
        priorityQueue.add(14);

        System.out.println(priorityQueue.poll());

        int[] array = {1,2,3,5,7,9,10};
        println(searchFirstBig(array , 800));


        AtomicInteger atomicCount = new AtomicInteger(0);
        atomicCount.incrementAndGet();

        Semaphore s = new Semaphore(10);
        new JygTest().lockTest();
        new JygTest().synTest();

        Lock lock = new ReentrantLock();


        println("Thread.currentThread().isInterrupted() : " + Thread.currentThread().isInterrupted());
        lock.lock();

        Thread t = new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            println(".............");
            lock.lock();
            lock.unlock();
            println(".............");
        }  ) ;
        try {
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();

        println("end");
    }


    public void synTest(){
        long start = System.currentTimeMillis();
        for(int i=0;i<10000000;){
            synchronized (JygTest.class){
                i++;
            }
        }
        println("sync : "+ (System.currentTimeMillis() - start));
    }

    public void lockTest(){
        ReentrantLock lock = new ReentrantLock();
        long start = System.currentTimeMillis();
        for(int i=0;i<10000000;){
            lock.lock();
                i++;
            lock.unlock();
        }
        println("lock : "+ (System.currentTimeMillis() - start));
    }

    @Test
    public void suffleTest(){
        List<Integer> list = new ArrayList<>();

        int sum = 100;
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int num = random.nextInt(sum);
            list.add(num);
            sum -= num;
            if (sum <= 0) {
                break;
            }
        }

//        Collections.shuffle(list);

        for(int i :list){
            println("num : "+ i);
        }

    }


    public static void println(Object o){
        System.out.println(o);
    }

    public static int searchFirstBig(int[] array,int num){

        int left = 0;
        int right = array.length-1;

        for (int midIndex;left<=right;){
            midIndex = left + ( right - left )/2;
            if(num > array[midIndex] ){
                left = midIndex+1;
            }else if(num < array[midIndex]){
                right = midIndex-1;
            }else {
                return midIndex;
            }
        }


        if(left >= array.length){
            return -1;
        }

        return left;
    }

    @Test
    public void testFile() throws Exception{
        String[] lines = "aas   sdas\t".trim().split(" ");

        for(String line : lines){
            AllUtil.println(" <<< "+ line);
        }
        Queue<Integer> queue = new ArrayBlockingQueue<>(10);

        for(int i = 0;i<100;i++){
            queue.add(i);
        }

        AllUtil.println(queue.size());
    }

}
