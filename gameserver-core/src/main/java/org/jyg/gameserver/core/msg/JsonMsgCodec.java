package org.jyg.gameserver.core.msg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

/**
 * create by jiayaoguang on 2020/10/25
 */
public class JsonMsgCodec extends AbstractByteMsgCodec<ByteMsgObj> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonMsgCodec( Class<? extends ByteMsgObj> byteMsgClass) {
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
