package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.handle.initializer.WebSocketServerInitializer;
import org.jyg.gameserver.core.util.Context;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class WebSocketService extends TcpService {

    public WebSocketService(int port, Context context) {
        super(port, new WebSocketServerInitializer(context));
    }
}
