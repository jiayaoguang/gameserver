package com.jyg.net;

import com.jyg.handle.initializer.WebSocketServerInitializer;
import com.jyg.util.Context;
import com.jyg.util.IGlobalQueue;

/**
 * create by jiayaoguang at 2018年3月1日
 */

public class WebSocketService extends TcpService {

    public WebSocketService(int port, Context context) {
        super(port, new WebSocketServerInitializer(context));
    }
}
