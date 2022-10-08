package org.jyg.gameserver.test;

import cn.hutool.core.util.ZipUtil;
import io.netty.util.internal.shaded.org.jctools.queues.MpscUnboundedArrayQueue;
import org.junit.Test;
import org.jyg.gameserver.core.data.ServerConfig;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.ExecTimeUtil;
import org.jyg.gameserver.core.util.IdUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    @Test
    public void testBytesZip() throws Exception{

//        byte[] bs = new byte[10000];
//
//        MsgBytes msgBytes = MsgBytes.newBuilder().setByteData(ByteString.copyFrom(bs)).build();
//
//        byte[] zbs = ZipUtil.gzip(bs);
//
//        byte[] zbs2 = ZipUtil.gzip(msgBytes.toByteArray());
//
//        byte[] zbs3 = ZipUtil.gzip(MsgBytes.newBuilder().setByteData(ByteString.copyFrom(zbs2)).build().toByteArray());
//
//        AllUtil.println(zbs.length);
//        AllUtil.println(zbs2.length);
//        AllUtil.println(zbs3.length);
    }


    @Test
    public void gzipTest() throws Exception{

        byte[] bytes = ("wdasdqwEDFSDGSFADASdsfsdfsdfsdfsdfhiwuefkdsnfsdsfsffsdfbxccbxxxxx" +
                "bcvvvvv423423443535345vvvvvvvdgsdgeter43452421312312kdsncmxjhsdgfjshdbfsnbfsdfdffbvcbchfhtrer3352523423mndsvfjhgsdjcbxzbcsfu23432d234234").getBytes();

        byte[] zipbytes = ZipUtil.gzip(bytes);

        byte[] zipbytsss = ZipUtil.unGzip(zipbytes);
//        AllUtil.println(new java.lang.String(zipbytsss));

        AllUtil.println(" befoe "+ bytes.length + "  af "+ zipbytes.length );
    }

    @Test
    public void testQueueCost() throws Exception{

        final ArrayBlockingQueue<String> queue1 = new ArrayBlockingQueue<>(4000000);

        for (int i = 0; i < 8; i++){
            startQueueOffer("ConcurrentLinkedQueue offer" , queue1);
        }

        Thread.sleep(1000);

        new Thread(()->{
            ExecTimeUtil.exec("ConcurrentLinkedQueue poll" , ()->{
                for (int i = 0; i < 8; i++) {
                    queue1.poll();
                }
            });
        }).start();


        Thread.sleep(2000);

        final MpscUnboundedArrayQueue<String> queue2 = new MpscUnboundedArrayQueue<>(4000000);
        for (int i = 0; i < 8; i++){
            startQueueOffer("MpscUnboundedArrayQueue offer",queue2);
        }

        Thread.sleep(2000);

        new Thread(()->{
            ExecTimeUtil.exec("MpscUnboundedArrayQueue poll" , ()->{
                for (int i = 0; i < 3000000; i++) {
                    queue2.relaxedPoll();
                }
            });
        }).start();


        Thread.sleep(3000);

    }

    public void startQueueOffer(String name , Queue<String> queue){
        new Thread(() -> {
            ExecTimeUtil.exec(name, () -> {
                for (int i = 0; i < 1000000; i++) {
                    queue.offer("");
                }
            });
        }).start();
    }


    @Test
    public void testIdUtil(){
        for (int i = 0; i < 30; i++)
            AllUtil.println(IdUtil.nextId());

    }
    @Test
    public void testInvoke(){
        Object o = "";
        p(o);
    }

    public void p(Object o){
        AllUtil.println("Object");
    }

    public void p(String o){
        AllUtil.println("Object");
    }


    public static class F{
        public int id2;

        public static int id3;
    }

    public static class S extends F{
//        private int id;

        public int id;
    }

    @Test
    public void testField(){
        Field[] fields = S.class.getFields();
        for( Field field : fields ){
            boolean isSTatic = Modifier.isStatic(field.getModifiers());

            AllUtil.println(field.getName() + " " + isSTatic);
        }
    }

}
