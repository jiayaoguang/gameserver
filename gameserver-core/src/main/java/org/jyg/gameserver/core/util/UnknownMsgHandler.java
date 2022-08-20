package org.jyg.gameserver.core.util;

import org.jyg.gameserver.core.session.Session;

public interface UnknownMsgHandler {

    void process(Session session ,int msgId , byte[] msgData);

}
