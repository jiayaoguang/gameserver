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

    private final ThreadLocal<LinkedBuffer> bufferThreadLocal;


    public ProtostuffMsgCodec(Class<? extends ByteMsgObj> byteMsgClass) {
        this(byteMsgClass,LinkedBuffer.DEFAULT_BUFFER_SIZE);
    }

    public ProtostuffMsgCodec(Class<? extends ByteMsgObj> byteMsgClass , int buffSize) {
        super(byteMsgClass);
        this.schema = RuntimeSchema.getSchema(byteMsgClass);

        this.bufferThreadLocal = ThreadLocal.withInitial(() -> LinkedBuffer.allocate(buffSize));

    }

    @Override
    public byte[] encode(ByteMsgObj obj) throws Exception {

        LinkedBuffer buffer = bufferThreadLocal.get();
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
            ByteMsgObj message = getByteMsgClass().newInstance();
            ProtobufIOUtil.mergeFrom(bytes, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
