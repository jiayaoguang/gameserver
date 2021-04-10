package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.RemoteInvokeData;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.IRemoteInvoke;

/**
 * create by jiayaoguang on 2021/4/10
 */
public class RemoteInvokeProcessor extends ByteMsgObjProcessor<RemoteInvokeData>{


    public RemoteInvokeProcessor() {
        super(RemoteInvokeData.class);
    }

    @Override
    public void process(Session session, EventData<RemoteInvokeData> event) {

        RemoteInvokeData remoteInvokeData = event.getData();
        String className =  remoteInvokeData.getInvokeName();

        try {
            Class<?> c = Class.forName(className);
            Object instance = c.newInstance();

            if(instance instanceof IRemoteInvoke){
                IRemoteInvoke remoteInvoke = (IRemoteInvoke)instance;
                remoteInvoke.invoke(remoteInvokeData.getParamJson());
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getMsgId(){
        return 0;
    }

}
