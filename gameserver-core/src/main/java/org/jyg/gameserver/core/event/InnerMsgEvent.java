package org.jyg.gameserver.core.event;

import io.netty.channel.Channel;

public class InnerMsgEvent extends MsgEvent<Object>{



    public InnerMsgEvent(int msgId, Object data) {
        super(msgId, data);

    }

}
