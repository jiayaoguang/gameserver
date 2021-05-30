package org.jyg.gameserver.core.data;

/**
 * create by jiayaoguang on 2020/5/23
 * 配置文件字段都要有对应的 set 方法
 */
public class ServerConfig {

    private boolean useGzip = false;

    private int maxFrameLength = 1024*1024*8;

    private String host = null;

    private boolean needMergeProto = false;

    private boolean preferEpoll = false;

    private String scanInvokeClassPath = "org.jyg";

    private int nettyIOThreadNum = 0;

    private int serverUid = 0;
    /**
     * 服务器读协议超时时间
     * 超时会关闭连接
     * (单位 : 秒)
     * 0 表示不启用
     */
    private int readOutTimeSec = 20;
    /**
     * 客户端写超时时间协议超时时间
     * 超时会关闭连接
     * (单位 : 秒)
     * 0 表示不启用
     */
    private int clientWriteOutTimeSec = 5;


    public boolean isUseGzip() {
        return useGzip;
    }

    public void setUseGzip(boolean useGzip) {
        this.useGzip = useGzip;
    }

    public int getMaxFrameLength() {
        return maxFrameLength;
    }

    public void setMaxFrameLength(int maxFrameLength) {
        this.maxFrameLength = maxFrameLength;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }


    public boolean isNeedMergeProto() {
        return needMergeProto;
    }

    public void setNeedMergeProto(boolean needMergeProto) {
        this.needMergeProto = needMergeProto;
    }

    public boolean isPreferEpoll() {
        return preferEpoll;
    }

    public void setPreferEpoll(boolean preferEpoll) {
        this.preferEpoll = preferEpoll;
    }

    public String getScanInvokeClassPath() {
        return scanInvokeClassPath;
    }

    public void setScanInvokeClassPath(String scanInvokeClassPath) {
        this.scanInvokeClassPath = scanInvokeClassPath;
    }


    public int getNettyIOThreadNum() {
        return nettyIOThreadNum;
    }

    public void setNettyIOThreadNum(int nettyIOThreadNum) {
        this.nettyIOThreadNum = nettyIOThreadNum;
    }


    public int getServerUid() {
        return serverUid;
    }

    public void setServerUid(int serverUid) {
        this.serverUid = serverUid;
    }

    public int getReadOutTimeSec() {
        return readOutTimeSec;
    }

    public void setReadOutTimeSec(int readOutTimeSec) {
        this.readOutTimeSec = readOutTimeSec;
    }

    public int getClientWriteOutTimeSec() {
        return clientWriteOutTimeSec;
    }

    public void setClientWriteOutTimeSec(int clientWriteOutTimeSec) {
        this.clientWriteOutTimeSec = clientWriteOutTimeSec;
    }
}
