package org.jyg.gameserver.core.util;

import com.google.protobuf.MessageOrBuilder;
import com.jyg.enums.EventType;
import com.jyg.util.IGlobalQueue;

/**
 * create by jiayaoguang on 2020/5/17
 */
public class LocalDataTransfer implements DataTransfer {

    private final IGlobalQueue globalQueue;

    public LocalDataTransfer(IGlobalQueue globalQueue) {
        this.globalQueue = globalQueue;
    }

    @Override
    public void send(MessageOrBuilder messageOrBuilder) {

        globalQueue.publicEvent(EventType.INNER_MSG, messageOrBuilder, null);

    }
}
