package org.jyg.gameserver.core.constant;

import org.jyg.gameserver.core.msg.route.*;

/**
 * create by jiayaoguang on 2021/5/29
 */
public class MsgIdConst {

    private MsgIdConst() {
    }

    public static final int REMOTE_INVOKE = 1;

    public static final int READ_OUTTIME = 2;

    public static final int WRITE_OUTTIME = 3;


    public static final int PING = 4;

    public static final int PONG = 5;


    public static final int ROUTE_REGISTER_MSG_ID = 300 ;
    public static final int ROUTE_REGISTER_REPLY_MSG_ID = 301 ;


    public static final int ROUTE_MSG_ID = 302 ;
    public static final int ROUTE_REPLY_MSG_ID = 303 ;


    public static final int ROUTE_CLIENT_SESSION_CONNECT_MSG_ID = 304;

    public static final int ROUTE_CLIENT_SESSION_DISCONNECT_MSG_ID = 305;


}
