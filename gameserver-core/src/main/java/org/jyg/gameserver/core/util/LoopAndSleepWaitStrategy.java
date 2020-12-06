package org.jyg.gameserver.core.util;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;
import org.jyg.gameserver.core.consumer.ConsumerHandler;

import java.util.concurrent.locks.LockSupport;

/**
 * created by jiayaoguang at 2018年4月9日
 */
public final class LoopAndSleepWaitStrategy implements WaitStrategy {
    private static final int DEFAULT_RETRIES = 500;

    private final int retries;

    private final ConsumerHandler consumerHandler;

    public LoopAndSleepWaitStrategy(ConsumerHandler consumerHandler) {
        this.retries = DEFAULT_RETRIES;
        this.consumerHandler = consumerHandler;
    }


    @Override
    public long waitFor(
            final long sequence, Sequence cursor, final Sequence dependentSequence, final SequenceBarrier barrier)
            throws AlertException {
        long availableSequence;
        int counter = retries;

        while ((availableSequence = dependentSequence.get()) < sequence) {
            counter = applyWaitMethod(barrier, counter);
        }

        return availableSequence;
    }

    @Override
    public void signalAllWhenBlocking() {
    }

    private int applyWaitMethod(final SequenceBarrier barrier, int counter) throws AlertException {
        barrier.checkAlert();

        if (counter > 200) {
            --counter;
        } else if (counter > 0) {
            --counter;
            Thread.yield();
        } else {
            consumerHandler.update();
            LockSupport.parkNanos(1000L);
        }

        return counter;
    }
}
