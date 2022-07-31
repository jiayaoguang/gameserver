package org.jyg.gameserver.core.session;

import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.util.Context;

public class LocalSession extends Session {

    private final Context context;
    private final int fromConsumerId;

    public LocalSession(int fromConsumerId, Context context) {
        super(0L);
        this.context = context;
        this.fromConsumerId = fromConsumerId;
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
        context.getConsumerManager().publicEvent(fromConsumerId, EventType.DEFAULT_EVENT ,msgObj , 0);
    }

    @Override
    public String getRemoteAddr() {
        throw new UnsupportedOperationException();
    }
}
