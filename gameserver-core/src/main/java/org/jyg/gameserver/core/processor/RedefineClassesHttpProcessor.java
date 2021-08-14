package org.jyg.gameserver.core.processor;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.ClassRedefineUtil;
import org.jyg.gameserver.core.util.Logs;

import java.io.File;
import java.lang.instrument.UnmodifiableClassException;
import java.util.List;

/**
 * create by jiayaoguang at 2021/5/29
 * 重定义类
 */
public class RedefineClassesHttpProcessor extends HttpProcessor {


    public RedefineClassesHttpProcessor() {
        super("/redefineClasses");
    }

    @Override
    public void service(Request request, Response response) {

        File rootDir = new File("redefineClasses");

        if (!rootDir.exists() || !rootDir.isDirectory()) {
            response.writeAndFlush("!rootDir.exists() || !rootDir.isDirectory()");
            return;
        }

        StringBuilder sendMsgSb = new StringBuilder();

        sendMsgSb.append("<pre>");

        List<File> childClassFiles = AllUtil.getChildFiles(rootDir, "class");

        for (File childClassFile : childClassFiles) {
            try {
                byte[] classBytes = FileUtil.readBytes(childClassFile);
                String className = ClassRedefineUtil.readClassName(classBytes);

                Class<?> redefineClass = Class.forName(className);
                ClassRedefineUtil.redefine(redefineClass, classBytes);
                Logs.DEFAULT_LOGGER.info(" refine class success file {}  class {}", childClassFile.getAbsolutePath(), className);
                sendMsgSb.append("refine file :").append(childClassFile.getAbsolutePath()).append("success");
            } catch (Exception e) {
                String exceptionMsg = ExceptionUtils.getStackTrace(e);
                Logs.DEFAULT_LOGGER.info(" refine class fail file {}  msg : {}", childClassFile.getAbsolutePath(), exceptionMsg);
                sendMsgSb.append("refine file :").append(childClassFile.getAbsolutePath()).append("fail exceptionMsg : ").append(exceptionMsg);
            }
        }
        sendMsgSb.append("</pre>");

        response.writeAndFlush(sendMsgSb.toString());

    }



}
