package org.jyg.gameserver.core.processor;

import cn.hutool.core.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;

/**
 * create by jiayaoguang at 2021/8/14
 * 127.0.0.1/msgAccessEnable?msgId=108&enable=false
 */
public class MsgAccessEnableHttpProcessor extends HttpProcessor {

    public MsgAccessEnableHttpProcessor() {
        super("/msgAccessEnable");
    }

    @Override
    public void service(Request request, Response response) {



        String msgIdStr = request.getParameter("msgId");

        String httpPath = request.getParameter("httpPath");

        String enableStr = request.getParameter("enable");

        if(StringUtils.isEmpty(msgIdStr) && StringUtils.isEmpty(httpPath)){
            response.write500Error("msgId and httpPath param not found " );
            return;
        }

        if(StringUtils.isEmpty(enableStr)){
            response.write500Error("enable param not found " );
            return;
        }

        boolean enable = true;
        if("false".equalsIgnoreCase(enableStr)){
            enable = false;
        }

        String returnMsg = "not found";

        if(StringUtils.isNotEmpty(msgIdStr)){

            int msgId = Integer.parseInt(msgIdStr);

            if(msgId <= 100){
                response.write500Error("msgId <= 100 ,can not disable " );
                return;
            }

            AbstractProcessor processor = getGameConsumer().getProcessor(msgId);
            if(processor != null){
                processor.setEnableAccess(enable);

                returnMsg = msgIdStr;
            }else {
                returnMsg = msgIdStr + " not found";
            }

        }else if(StringUtils.isNotEmpty(httpPath)){
            if(httpPath.charAt(0) != '/'){
                httpPath = '/' + httpPath;
            }
            AbstractProcessor processor = getGameConsumer().getHttpProcessor(httpPath);
            processor.setEnableAccess(enable);

            if(processor != null){
                processor.setEnableAccess(enable);

                returnMsg = msgIdStr;
            }else {
                returnMsg = msgIdStr + " not found";
            }
        }




        StringBuilder sendMsgSb = new StringBuilder();

        sendMsgSb.append("<h2><center>msgAccessEnable</center></h2>");

        sendMsgSb.append("<pre>");

        sendMsgSb.append("set ").append(returnMsg).append("  enable : ").append(enable);




        sendMsgSb.append("</pre>");


        response.writeAndFlush(sendMsgSb.toString());

    }
}
