package org.jyg.gameserver.core.msg;

import com.alibaba.fastjson.JSONObject;
import org.jyg.gameserver.core.enums.MsgType;

/**
 * create by jiayaoguang on 2020/10/25
 */
public class JsonMsgCodec extends AbstractByteMsgCodec<ByteMsgObj> {

    public JsonMsgCodec(int msgId, Class<? extends ByteMsgObj> byteMsgClass) {
        super(msgId, byteMsgClass);
    }

    @Override
    public byte[] encode(ByteMsgObj jsonMsg) {
        return JSONObject.toJSONBytes(jsonMsg);
    }

    @Override
    public ByteMsgObj decode(byte[] bytes) {
        return JSONObject.parseObject(bytes , getByteMsgClass());
    }

}
