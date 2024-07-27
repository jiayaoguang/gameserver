package org.jyg.gameserver.core.msg.encrypt;

import cn.hutool.json.ObjectMapper;
import com.alibaba.fastjson2.JSONObject;
import io.netty.util.CharsetUtil;
import org.jyg.gameserver.core.msg.AbstractByteMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;

import java.io.IOException;

/**
 * create by jiayaoguang on 2022/9/17
 */
public class JsonEncryptorMsgCodec<T> extends AbstractByteMsgCodec<ByteMsgObj> {

    private final MsgEncryptor msgEncryptor;


    public JsonEncryptorMsgCodec(Class<? extends ByteMsgObj> byteMsgClass, MsgEncryptor msgEncryptor) {
        super(byteMsgClass);
        this.msgEncryptor = msgEncryptor;
    }


    @Override
    public byte[] encode(ByteMsgObj jsonMsg) {

        return msgEncryptor.encrypt(JSONObject.toJSONString(jsonMsg).getBytes(CharsetUtil.UTF_8));
    }

    @Override
    public ByteMsgObj decode(byte[] bytes) throws IOException {
        return JSONObject.parseObject(new String(msgEncryptor.decrypt(bytes), CharsetUtil.UTF_8), getByteMsgClass());
    }


}
