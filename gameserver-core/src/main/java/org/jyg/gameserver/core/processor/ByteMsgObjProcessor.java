package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2020/12/6
 */
public abstract class ByteMsgObjProcessor<T extends ByteMsgObj> extends AbstractProcessor<T> {

    private final Class<? extends ByteMsgObj> byteMsgObjClazz;

    public ByteMsgObjProcessor(Class<? extends ByteMsgObj> byteMsgObjClazz) {
        this.byteMsgObjClazz = byteMsgObjClazz;
    }

    @Override
    public abstract void process(Session session, EventData<T> event);

    public Class<? extends ByteMsgObj> getByteMsgObjClazz() {
        return byteMsgObjClazz;
    }

    public int getMsgId() {
        return getContext().getMsgIdByByteMsgObj(byteMsgObjClazz);
    }
}
