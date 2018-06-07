package com.jyg.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

import com.jyg.bean.LogicEvent;
import com.jyg.consumers.EventConsumerFactory;
import com.jyg.enums.EventType;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2017年12月18日
 */
public class GlobalQueue {

	private static Disruptor<LogicEvent<Object>> disruptor;
	private static final int bufferSize = 4096;
	private static RingBuffer<LogicEvent<Object>> ringBuffer;
	private static ThreadPoolExecutor executor;
	static {
		
	}

	public static void start() {
		EventFactory<LogicEvent<Object>> eventFactory = () -> new LogicEvent<Object>();
		ArrayBlockingQueue<Runnable> fairBlockingQueue = new ArrayBlockingQueue<Runnable>(bufferSize,true);
		executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,fairBlockingQueue, new AbortPolicy());
		executor.allowCoreThreadTimeOut(true);
		
		disruptor = new Disruptor<>(eventFactory, bufferSize, executor, ProducerType.MULTI,
				new FreeSleepWaitStrategy());
		try {
			EventHandlerGroup<LogicEvent<Object>> handleEventsWith = disruptor
					.handleEventsWith(EventConsumerFactory.newEventConsumer());
		} catch (Exception e) {
			e.printStackTrace();
		}

		ringBuffer = disruptor.getRingBuffer();
		disruptor.start();
	}

	public static void shutdown() {

		disruptor.shutdown();
		executor.shutdown();

	}

	public static void publicEvent(EventType evenType, Object data, Channel channel) {
		long sequence = GlobalQueue.ringBuffer.next();
		try {
			LogicEvent<Object> event = GlobalQueue.ringBuffer.get(sequence);
			event.setData(data);
			event.setChannel(channel);
			event.setChannelEventType(evenType);
		} finally {
			GlobalQueue.ringBuffer.publish(sequence);
		}

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

}
