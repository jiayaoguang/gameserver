package org.jyg.gameserver.util;

import com.alibaba.fastjson.JSONObject;
import org.jyg.gameserver.core.msg.AbstractByteMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang at 2021/5/24
 */
public class FastJsonMsgCodec extends AbstractByteMsgCodec {


    public FastJsonMsgCodec( Class<? extends ByteMsgObj> byteMsgClass) {
        super( byteMsgClass);
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
