package org.jyg.gameserver.core.msg;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * create by jiayaoguang on 2021/7/11
 */
public class ProtostuffMsgCodec extends AbstractByteMsgCodec<ByteMsgObj> {


    private final Schema schema;


    public ProtostuffMsgCodec(Class<? extends ByteMsgObj> byteMsgClass) {
        super(byteMsgClass);
        schema = RuntimeSchema.getSchema(byteMsgClass);
    }

    @Override
    public byte[] encode(ByteMsgObj obj) throws Exception {
        Class cls = (Class) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            return ProtobufIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public ByteMsgObj decode(byte[] bytes) throws Exception {
        try {
            ByteMsgObj message = (ByteMsgObj) getByteMsgClass().newInstance();
            ProtobufIOUtil.mergeFrom(bytes, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
