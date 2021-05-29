package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.RemoteInvokeData;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.invoke.IRemoteInvoke;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2021/4/10
 */
public class RemoteInvokeProcessor extends ByteMsgObjProcessor<RemoteInvokeData> {


    public RemoteInvokeProcessor() {
        super(RemoteInvokeData.class);
    }

    @Override
    public void process(Session session, EventData<RemoteInvokeData> event) {

        RemoteInvokeData remoteInvokeData = event.getData();
        String invokeName = remoteInvokeData.getInvokeName();
        IRemoteInvoke remoteInvoke = getContext().getRemoteInvokeManager().getInvokeClass(invokeName);

        if (remoteInvoke == null) {
            Logs.DEFAULT_LOGGER.info("invokeName {} not found", invokeName);
            return;
        }

        try {
            remoteInvoke.invoke(remoteInvokeData.getParamMap());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
