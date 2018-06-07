package com.jyg.gameserver.buffer;

/**
 * created by jiayaoguang at 2018年5月26日
 * 伪共享测试
 */
import java.util.concurrent.atomic.AtomicLong;

public final class FalseSharing implements Runnable {
	public final static int NUM_THREADS = 4; // change
	public final static long ITERATIONS = 500L * 1000L * 1000L;
	private final int arrayIndex;

	private static PaddedAtomicLong[] longs = new PaddedAtomicLong[NUM_THREADS];
	static {
		for (int i = 0; i < longs.length; i++) {
			longs[i] = new PaddedAtomicLong();
		}
	}

	public FalseSharing(final int arrayIndex) {
		this.arrayIndex = arrayIndex;
	}

	public static void main(final String[] args) throws Exception {
		final long start = System.nanoTime();
		runTest();
		System.out.println("duration = " + (System.nanoTime() - start)/1000000.0);
	}

	private static void runTest() throws InterruptedException {
		Thread[] threads = new Thread[NUM_THREADS];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new FalseSharing(i));
		}

		for (Thread t : threads) {
			t.start();
		}

		for (Thread t : threads) {
			t.join();
		}
	}

	public void run() {
		long i = ITERATIONS + 1;
		while (0 != --i) {
			longs[arrayIndex].set(i);
		}
	}

	// 使无用变量防止被编译优化
	public static long sumPaddingToPreventOptimisation(final int index) {
		PaddedAtomicLong v = longs[index];
		return v.p1 + v.p2 + v.p3 + v.p4 + v.p5 + v.p6;
	}
	// 一条缓存行有64字节，而Java程序的对象头固定占8字节(32位系统)或12字节(64位系统默认开启压缩, 不开压缩为16字节)
	public static class PaddedAtomicLong extends AtomicLong {
		public volatile long p1, p2, p3, p4, p5, p6 = 7L;//15162
	}
}
