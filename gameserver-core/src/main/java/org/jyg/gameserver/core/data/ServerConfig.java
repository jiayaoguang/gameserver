package org.jyg.gameserver.core.data;

/**
 * create by jiayaoguang on 2020/5/23
 * 配置文件字段都要有对应的 set 方法
 */
public class ServerConfig {

    private boolean useGzip = false;

    private int maxFrameLength = 1024*1024*8;

    private String host = null;

    private boolean needMergeProto = true;

    private boolean preferEpoll = false;

    private String sacnInvokeClassPath = "";

    private int nettyIOThreadNum = 0;

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

    public String getSacnInvokeClassPath() {
        return sacnInvokeClassPath;
    }

    public void setSacnInvokeClassPath(String sacnInvokeClassPath) {
        this.sacnInvokeClassPath = sacnInvokeClassPath;
    }


    public int getNettyIOThreadNum() {
        return nettyIOThreadNum;
    }

    public void setNettyIOThreadNum(int nettyIOThreadNum) {
        this.nettyIOThreadNum = nettyIOThreadNum;
    }


}
