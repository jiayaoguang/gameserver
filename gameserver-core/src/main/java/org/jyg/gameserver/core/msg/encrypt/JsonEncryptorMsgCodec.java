package org.jyg.gameserver.core.msg.encrypt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jyg.gameserver.core.msg.AbstractByteMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;

import java.io.IOException;

/**
 * create by jiayaoguang on 2022/9/17
 */
public class JsonEncryptorMsgCodec<T> extends AbstractByteMsgCodec<ByteMsgObj> {

    private final MsgEncryptor msgEncryptor;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public JsonEncryptorMsgCodec(Class<? extends ByteMsgObj> byteMsgClass, MsgEncryptor msgEncryptor) {
        super(byteMsgClass);
        this.msgEncryptor = msgEncryptor;
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }


    @Override
    public byte[] encode(ByteMsgObj jsonMsg) throws JsonProcessingException {

        return msgEncryptor.encrypt(objectMapper.writeValueAsBytes(jsonMsg));
    }

    @Override
    public ByteMsgObj decode(byte[] bytes) throws IOException {
        return objectMapper.readValue(msgEncryptor.decrypt(bytes), getByteMsgClass());
    }


}
