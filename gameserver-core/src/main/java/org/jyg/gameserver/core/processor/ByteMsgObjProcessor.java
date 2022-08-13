package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.session.Session;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * create by jiayaoguang on 2020/12/6
 */
public abstract class ByteMsgObjProcessor<T extends ByteMsgObj> extends AbstractProcessor<T> {

    private final Class<? extends ByteMsgObj> byteMsgObjClazz;


    public ByteMsgObjProcessor() {


        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) { // sanity check, should never happen
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }

        Type _type = ((ParameterizedType) superClass).getActualTypeArguments()[0];


        this.byteMsgObjClazz = (Class<? extends ByteMsgObj>) _type;
    }

    public ByteMsgObjProcessor(Class<? extends ByteMsgObj> byteMsgObjClazz) {
        this.byteMsgObjClazz = byteMsgObjClazz;
    }

    @Override
    public abstract void process(Session session, EventData<T> event);

    public Class<? extends ByteMsgObj> getByteMsgObjClazz() {
        return byteMsgObjClazz;
    }

    @Deprecated
    public int getMsgId() {
        return getContext().getMsgIdByByteMsgObj(byteMsgObjClazz);
    }
}
