package org.jyg.gameserver.core.util;

import org.jyg.gameserver.core.consumer.EventConsumerFactory;
import org.jyg.gameserver.core.enums.EventType;
import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2017年12月18日
 */
public interface IGlobalQueue {

    int DEFAULT_QUEUE_SIZE = 1024 * 64;

    void start();

    void stop();

    void publicEvent(EventType evenType, Object data, Channel channel);

    void publicEvent(EventType evenType, Object data, Channel channel, int eventId);

    public void setEventConsumerFactory(EventConsumerFactory eventConsumerFactory);

    public EventConsumerFactory getEventConsumerFactory();
}
