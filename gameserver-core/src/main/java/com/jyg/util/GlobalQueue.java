package com.jyg.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.jyg.bean.LogicEvent;
import com.jyg.consumers.DefaultEventConsumerFactory;
import com.jyg.consumers.EventConsumerFactory;
import com.jyg.enums.EventType;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by jiayaoguang at 2017年12月18日
 */
public class GlobalQueue {

	private static final Logger logger = LoggerFactory.getLogger(GlobalQueue.class);

	private static Disruptor<LogicEvent<Object>> disruptor;
	private static final int BUFFER_SIZE = 4096;
	private static RingBuffer<LogicEvent<Object>> ringBuffer;
//	private static ThreadPoolExecutor executor;

	public static void start() {

		DefaultEventConsumerFactory defaultEventConsumerFactory = new DefaultEventConsumerFactory();

		EventFactory<LogicEvent<Object>> eventFactory = LogicEvent::new;
//		BlockingQueue<Runnable> fairBlockingQueue = new ArrayBlockingQueue<>(BUFFER_SIZE, true);
//		executor = new ThreadPoolExecutor(1, 1, 3*60*1000L, TimeUnit.MILLISECONDS, fairBlockingQueue, new AbortPolicy());
//		executor.allowCoreThreadTimeOut(true);

//		disruptor = new Disruptor<>(eventFactory, BUFFER_SIZE, executor, ProducerType.MULTI,
//				new LoopAndSleepWaitStrategy());

		disruptor = new Disruptor<>(eventFactory, BUFFER_SIZE, new RingBufferThreadFactory(), ProducerType.MULTI,
				new LoopAndSleepWaitStrategy());

		EventHandlerGroup<LogicEvent<Object>> handleEventsWith = disruptor
				.handleEventsWith(defaultEventConsumerFactory.newEventConsumer());

		ringBuffer = disruptor.getRingBuffer();
		disruptor.start();
	}

	/**
	 *
	 * @param groupNum 消费者组数量
	 * @param groupNum 组里的消费者数量
	 */
	private void createConsumerGroups(int groupNum, int oneGroupConsumerNum , EventConsumerFactory eventConsumerFactory) {
		for (int i = 0; i < groupNum; i++) {
			EventHandlerGroup<LogicEvent<Object>> handleEventsWith = disruptor.handleEventsWith(eventConsumerFactory.newEventConsumer());
			for(int j = 0; j < oneGroupConsumerNum-1 ;j++){
				handleEventsWith.handleEventsWith(eventConsumerFactory.newEventConsumer());
			}
		}
	}

	public static void shutdown() {

		disruptor.shutdown();
//		executor.shutdown();

	}

	public static void publicEvent(EventType evenType, Object data, Channel channel) {
		publicEvent(evenType, data, channel, 0);
	}

	public static void publicEvent(EventType evenType, Object data, Channel channel, int eventId) {
		long sequence = GlobalQueue.ringBuffer.next();
		try {
			LogicEvent<Object> event = GlobalQueue.ringBuffer.get(sequence);
			event.setData(data);
			event.setChannel(channel);
			event.setEventId(eventId);
			event.setChannelEventType(evenType);
		} finally {
			GlobalQueue.ringBuffer.publish(sequence);
		}
	}


	static class RingBufferThreadFactory implements ThreadFactory {

		private final AtomicInteger threadId = new AtomicInteger(0);

		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
//			thread.setPriority(Thread.MAX_PRIORITY);
			thread.setDaemon(false);
			thread.setName("ringbuffer_consumer_thread_" + threadId.getAndIncrement());
			logger.info("create conusmer thread : {} ", thread.getName());
			return thread;
		}
	}

}
