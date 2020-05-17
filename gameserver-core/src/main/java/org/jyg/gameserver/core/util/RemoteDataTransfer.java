package org.jyg.gameserver.core.util;

import com.google.protobuf.MessageOrBuilder;
import io.netty.channel.Channel;


/**
 * create by jiayaoguang on 2020/5/17
 */
public class RemoteDataTransfer implements DataTransfer {

    private final Channel channel;

    public RemoteDataTransfer(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void send(MessageOrBuilder messageOrBuilder) {
        channel.write(messageOrBuilder);
    }
}
