package org.jyg.gameserver.core.util;

import com.google.protobuf.MessageOrBuilder;

/**
 * create by jiayaoguang on 2020/5/17
 */
public interface DataTransfer {

    void send(MessageOrBuilder messageOrBuilder);

}
