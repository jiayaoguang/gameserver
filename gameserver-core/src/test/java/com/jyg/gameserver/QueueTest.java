package com.jyg.gameserver;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * created by jiayaoguang at 2018年4月16日
 */
public class QueueTest {
	
	static class  SynLinked extends LinkedList<String>{
		
		@Override
		synchronized public boolean offer(String e) {
			return super.offer(e);
		}
		
		@Override
		synchronized public String poll() {
			return super.poll();
		}
		
	}
	
	public static void main(String[] args) {
		
		ConcurrentLinkedQueue<String> concurrentLinkedQueue =new ConcurrentLinkedQueue<>();
		
		SynLinked synlinked = new SynLinked();
		
		ArrayBlockingQueue<String> block = new ArrayBlockingQueue<>(1000);
		
		//八个线程的情况下(时间)  synlinked > concurrentLinkedQueue > ArrayBlockingQueue
		// 三个线程的情况下(时间) concurrentLinkedQueue > SynLinked > ArrayBlockingQueue
		//单个线程的情况下 (时间)  ArrayBlockingQueue > SynLinked > concurrentLinkedQueue
		
		T t = new T( block );
		for(int i = 0;i<1 ; i++){
			new Thread( t  ).start();
		}
	}
	

	
	static class T implements Runnable{
		
		public static Queue<String> queue;
		
		public static long time1 = System.currentTimeMillis();
		
		T(Queue<String> queue){
			this.queue = queue;
			
		}
		
		public void run(){
			for(int i = 0;i<10000000 ; i++){
//				if(queue.size()>1000) {
//					queue.poll();
//				}else {
//					queue.offer("");
//				}
				queue.offer("");
				queue.poll();
			}
			System.out.println("用时" + (System.currentTimeMillis() - time1));
		}
		
		
	}
	
	
	
}

