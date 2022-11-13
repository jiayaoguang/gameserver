package org.jyg.gameserver.core.data;

/**
 * create by jiayaoguang on 2022/11/12
 */
public class RemoteConsumerInfo {

    private int consumerId;

    private String ip;

    private int port;


    public int getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(int consumerId) {
        this.consumerId = consumerId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
