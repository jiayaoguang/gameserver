package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.handle.initializer.WebSocketServerInitializer;
import org.jyg.gameserver.core.util.GameContext;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class WebSocketConnector extends TcpConnector {

    public WebSocketConnector(int port, GameContext gameContext) {
        super(port, new WebSocketServerInitializer(gameContext));
    }
}
