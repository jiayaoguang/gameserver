package com.jyg.util;

import java.util.concurrent.locks.LockSupport;

import com.jyg.net.EventDispatcher;
import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;

/**
 * created by jiayaoguang at 2018年4月9日
 */
public final class FreeSleepWaitStrategy implements WaitStrategy {
	private static final int DEFAULT_RETRIES = 200;

	private final int retries;

	public FreeSleepWaitStrategy() {
		this(DEFAULT_RETRIES);
	}

	public FreeSleepWaitStrategy(int retries) {
		this.retries = retries;
	}


	@Override
	public long waitFor(
			final long sequence, Sequence cursor, final Sequence dependentSequence, final SequenceBarrier barrier)
			throws AlertException
	{
		long availableSequence;
		int counter = retries;

		while ((availableSequence = dependentSequence.get()) < sequence)
		{
			EventDispatcher.getInstance().loop();
			counter = applyWaitMethod(barrier, counter);
		}

		return availableSequence;
	}

	@Override
	public void signalAllWhenBlocking() {
	}

	private int applyWaitMethod(final SequenceBarrier barrier, int counter) throws AlertException {
		barrier.checkAlert();
		System.out.println("cost : "+ counter);
		if (counter > 100) {
			--counter;
		} else if (counter > 0) {
			--counter;
			Thread.yield();
		} else {
			LockSupport.parkNanos(3*1000000L);
		}

		return counter;
	}
}
