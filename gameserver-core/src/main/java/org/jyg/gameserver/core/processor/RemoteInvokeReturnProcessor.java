package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.RemoteInvokeReturnData;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2021/4/10
 */
public class RemoteInvokeReturnProcessor extends ByteMsgObjProcessor<RemoteInvokeReturnData>{


    public RemoteInvokeReturnProcessor() {
        super(RemoteInvokeReturnData.class);
    }

    @Override
    public void process(Session session, MsgEvent<RemoteInvokeReturnData> event) {

        RemoteInvokeReturnData returnData = event.getMsgData();


    }

    @Override
    public int getMsgId(){
        return 1;
    }
    
}
