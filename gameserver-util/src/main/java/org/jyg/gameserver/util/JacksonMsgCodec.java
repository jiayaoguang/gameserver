package org.jyg.gameserver.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jyg.gameserver.core.msg.AbstractByteMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;

import java.io.IOException;

/**
 * create by jiayaoguang on 2024/7/27
 */
public class JacksonMsgCodec extends AbstractByteMsgCodec<ByteMsgObj> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JacksonMsgCodec( Class<? extends ByteMsgObj> byteMsgClass) {
        super( byteMsgClass);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Override
    public byte[] encode(ByteMsgObj jsonMsg) throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(jsonMsg);
    }

    @Override
    public ByteMsgObj decode(byte[] bytes) throws IOException {
        return objectMapper.readValue(bytes, getByteMsgClass());
    }

}

