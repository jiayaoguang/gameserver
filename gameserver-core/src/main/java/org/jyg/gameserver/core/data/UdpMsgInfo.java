package org.jyg.gameserver.core.data;

/**
 * create by jiayaoguang on 2024/9/7
 */
public class UdpMsgInfo {

    private String host;

    private int port;

    private Object msg;


    public UdpMsgInfo(String host, int port, Object msg) {
        this.host = host;
        this.port = port;
        this.msg = msg;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
