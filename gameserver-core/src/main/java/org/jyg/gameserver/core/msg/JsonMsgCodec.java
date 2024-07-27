package org.jyg.gameserver.core.msg;

import com.alibaba.fastjson2.JSONObject;
import io.netty.util.CharsetUtil;

import java.io.IOException;

/**
 * create by jiayaoguang on 2020/10/25
 */
public class JsonMsgCodec extends AbstractByteMsgCodec<ByteMsgObj> {


    public JsonMsgCodec( Class<? extends ByteMsgObj> byteMsgClass) {
        super( byteMsgClass);
    }

    @Override
    public byte[] encode(ByteMsgObj jsonMsg)  {
        return JSONObject.toJSONString(jsonMsg).getBytes(CharsetUtil.UTF_8);
    }

    @Override
    public ByteMsgObj decode(byte[] bytes) throws IOException {
        return JSONObject.parseObject(new String(bytes,CharsetUtil.UTF_8) , getByteMsgClass());
    }

}
