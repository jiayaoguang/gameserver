package org.jyg.gameserver.core.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.enums.MsgType;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;

import java.lang.reflect.InvocationTargetException;

/**
 * create by jiayaoguang on 2020/10/25
 */
public class ProtoMsgCodec extends AbstractMsgCodec<MessageLite> {

    private final MessageLite defaultMessageLite;

    public ProtoMsgCodec(Class<MessageLite> messageLiteClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super(MsgType.PROTO);
        this.defaultMessageLite = (MessageLite) messageLiteClass.getMethod("getDefaultInstance").invoke(null);
    }

    public ProtoMsgCodec( MessageLite defaultMessageLite) {
        super(MsgType.PROTO);
        this.defaultMessageLite = defaultMessageLite;
    }

    @Override
    public byte[] encode(MessageLite o) {

        return o.toByteArray();
    }

    @Override
    public MessageLite decode(byte[] bytes) throws InvalidProtocolBufferException {
        return defaultMessageLite.getParserForType().parseFrom(bytes);
    }

}
