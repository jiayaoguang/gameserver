package org.jyg.gameserver.core.consumer;

import io.netty.channel.Channel;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;

/**
 * create by jiayaoguang on 2020/5/24
 */
public class RabbitMQConsumer extends Consumer {


    public RabbitMQConsumer() {
    }


    @Override
    public void doStart() {

    }

    @Override
    public void doStop() {

    }

    @Override
    public void publicEvent(EventType evenType, Object data, Channel channel, int eventId, EventExtData eventExtData) {

    }

}
