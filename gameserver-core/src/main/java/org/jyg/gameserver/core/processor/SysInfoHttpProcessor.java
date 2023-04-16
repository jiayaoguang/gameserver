package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.intercept.WhiteIpInterceptor;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;

import java.lang.management.*;
import java.util.List;

/**
 * create by jiayaoguang at 2021/8/14
 */
public class SysInfoHttpProcessor extends HttpProcessor {

    public SysInfoHttpProcessor() {
        super("/sysInfo");
        setMsgInterceptor(new WhiteIpInterceptor());
    }

    @Override
    public void service(Request request, Response response) {

        StringBuilder sendMsgSb = new StringBuilder();

        sendMsgSb.append("<h2><center>sys info</center></h2>");

        sendMsgSb.append("<pre>");

        appendThreadMsg(sendMsgSb);

        appendOsMsg(sendMsgSb);

        appendMemoryMsg(sendMsgSb);

        appendGcMsg(sendMsgSb);


        sendMsgSb.append("</pre>");


        response.writeAndFlush(sendMsgSb.toString());

    }

    private void appendThreadMsg(StringBuilder sendMsgSb) {
        sendMsgSb.append("线程数据 :\n");

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false,false);
        for(ThreadInfo threadInfo : threadInfos){
            sendMsgSb.append("threadInfo ").append(threadInfo.getThreadName()).append(" blockcount ").append(threadInfo.getBlockedCount()).append("\n");
        }
    }

    private void appendOsMsg(StringBuilder sendMsgSb) {
        sendMsgSb.append("\n");

        sendMsgSb.append("os :\n");
        OperatingSystemMXBean osb = ManagementFactory.getOperatingSystemMXBean();
        sendMsgSb.append("name :").append(osb.getName()); //Windows 10
        sendMsgSb.append("\n");
        sendMsgSb.append("arch :").append(osb.getArch()); //amd 64
        sendMsgSb.append("\n");
        sendMsgSb.append("processor :").append(osb.getAvailableProcessors()); //4
        sendMsgSb.append("\n");
        sendMsgSb.append("version :").append(osb.getVersion()); //10.0
        sendMsgSb.append("\n");
        sendMsgSb.append("systemLoadAverage :").append(osb.getSystemLoadAverage()); //-1.0
        sendMsgSb.append("\n");
    }

    private void appendMemoryMsg(StringBuilder sendMsgSb) {
        sendMsgSb.append("\n");
        sendMsgSb.append("Memory :\n");

        MemoryMXBean mxb = ManagementFactory.getMemoryMXBean();
        //Heap
        sendMsgSb.append("Max:").append(mxb.getHeapMemoryUsage().getMax() / 1024 / 1024).append("MB");    //Max:1776MB
        sendMsgSb.append("\n");
        sendMsgSb.append("Init:").append(mxb.getHeapMemoryUsage().getInit() / 1024 / 1024).append("MB");  //Init:126MB
        sendMsgSb.append("\n");
        sendMsgSb.append("Committed:").append(mxb.getHeapMemoryUsage().getCommitted() / 1024 / 1024).append("MB");   //Committed:121MB
        sendMsgSb.append("\n");
        sendMsgSb.append("Used:").append(mxb.getHeapMemoryUsage().getUsed() / 1024 / 1024).append("MB");  //Used:7MB
        sendMsgSb.append("\n");
        sendMsgSb.append(mxb.getHeapMemoryUsage().toString());    //init = 132120576(129024K) used = 8076528(7887K) committed = 126877696(123904K) max = 1862270976(1818624K)
        sendMsgSb.append("\n");
        sendMsgSb.append("\n");

        //Non heap
        sendMsgSb.append("Non heap Max:").append(mxb.getNonHeapMemoryUsage().getMax() / 1024 / 1024).append("MB");    //Max:0MB
        sendMsgSb.append("\n");
        sendMsgSb.append("Non heap Init:").append(mxb.getNonHeapMemoryUsage().getInit() / 1024 / 1024).append("MB");  //Init:2MB
        sendMsgSb.append("\n");
        sendMsgSb.append("Non heap Committed:").append(mxb.getNonHeapMemoryUsage().getCommitted() / 1024 / 1024).append("MB");   //Committed:8MB
        sendMsgSb.append("\n");
        sendMsgSb.append("Non heap Used:").append(mxb.getNonHeapMemoryUsage().getUsed() / 1024 / 1024).append("MB");  //Used:7MB
        sendMsgSb.append("\n");
        sendMsgSb.append(mxb.getNonHeapMemoryUsage().toString());    //init = 2555904(2496K) used = 7802056(7619K) committed = 9109504(8896K) max = -1(-1K)
        sendMsgSb.append("\n");
    }

    private void appendGcMsg(StringBuilder sendMsgSb) {
        sendMsgSb.append("\n");
        sendMsgSb.append("gc :\n");
        List<GarbageCollectorMXBean> garbageCollectorMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for(GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMxBeans){
            sendMsgSb.append("name:").append(garbageCollectorMXBean.getName());
            sendMsgSb.append("\n");
            sendMsgSb.append("count:").append(garbageCollectorMXBean.getCollectionCount());
            sendMsgSb.append("\n");
            sendMsgSb.append("time:").append(garbageCollectorMXBean.getCollectionTime());
            sendMsgSb.append("\n");
        }
    }
}
