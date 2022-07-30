package org.jyg.gameserver.core.session;

import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Context;

public class MQSession extends Session {

    private final Context context;
    private final int mqPushConsumerId;

    public MQSession( int mqPushConsumerId, Context context) {
        super(0L);
        this.context = context;
        this.mqPushConsumerId = mqPushConsumerId;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        // do nothing
    }

    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException();
    }


    @Override
    protected void writeObjMessage(Object msgObj) {
        context.getConsumerManager().publicEvent(mqPushConsumerId , EventType.DEFAULT_EVENT ,msgObj , 0);
    }

    @Override
    public String getRemoteAddr() {
        throw new UnsupportedOperationException();
    }
}
