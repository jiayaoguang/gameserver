package com.jyg.util;

import com.jyg.bean.LogicEvent;
import com.jyg.consumer.EventConsumerFactory;
import com.jyg.enums.EventType;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

/**
 * created by jiayaoguang at 2017年12月18日
 */
public interface IGlobalQueue {

    int DEFAULT_QUEUE_SIZE = 1024 * 64;

    void start();

    void shutdown();

    void publicEvent(EventType evenType, Object data, Channel channel);

    void publicEvent(EventType evenType, Object data, Channel channel, int eventId);

}
