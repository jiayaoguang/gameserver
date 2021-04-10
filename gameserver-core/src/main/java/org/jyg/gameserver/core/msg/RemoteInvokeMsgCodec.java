package org.jyg.gameserver.core.msg;

import com.alibaba.fastjson.JSONObject;
import org.jyg.gameserver.core.data.RemoteInvokeData;
import org.jyg.gameserver.core.util.IRemoteInvoke;

/**
 * create by jiayaoguang on 2020/10/25
 */
public class RemoteInvokeMsgCodec extends JsonMsgCodec{

    public RemoteInvokeMsgCodec(int msgId, Class<? extends RemoteInvokeData> remoteInvokeData) {
        super(msgId, remoteInvokeData);
    }

}
