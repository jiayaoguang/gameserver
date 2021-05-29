package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.msg.ReadIdleMsgObj;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2021/5/29
 * 读超时
 */
public class ReadOutTimeProcessor extends ByteMsgObjProcessor<ReadIdleMsgObj> {
    public ReadOutTimeProcessor() {
        super(ReadIdleMsgObj.class);
    }

    @Override
    public void process(Session session, EventData<ReadIdleMsgObj> event) {

//        getConsumer().getChannelManager().getSession()

        String addr = AllUtil.getChannelRemoteAddr(session.getChannel());

        session.stop();
        Logs.DEFAULT_LOGGER.error(" force close addr {} , cause read timeout" , addr);

    }
}
