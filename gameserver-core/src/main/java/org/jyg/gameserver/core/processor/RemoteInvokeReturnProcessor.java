package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.RemoteInvokeData;
import org.jyg.gameserver.core.data.RemoteInvokeReturnData;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.IRemoteInvoke;

/**
 * create by jiayaoguang on 2021/4/10
 */
public class RemoteInvokeReturnProcessor extends ByteMsgObjProcessor<RemoteInvokeReturnData>{


    public RemoteInvokeReturnProcessor() {
        super(RemoteInvokeReturnData.class);
    }

    @Override
    public void process(Session session, EventData<RemoteInvokeReturnData> event) {

        RemoteInvokeReturnData returnData = event.getData();


    }

    @Override
    public int getMsgId(){
        return 1;
    }
    
}
