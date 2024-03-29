package org.jyg.gameserver.core.processor;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jyg.gameserver.core.intercept.WhiteIpInterceptor;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.util.ClassLoadListener;
import org.jyg.gameserver.core.util.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang at 2021/8/14
 */
public class LoadClassesHttpProcessor extends HttpProcessor {

    public LoadClassesHttpProcessor() {
        super("/loadClasses");
        setMsgInterceptor(new WhiteIpInterceptor());
    }

    @Override
    public void service(Request request, Response response) {

        List<Class<?>> classList = getGameConsumer().getClassLoadManager().loadClasses();

        if (CollectionUtil.isEmpty(classList)) {
            response.writeAndFlush("no class load");
            return;
        }

        List<ClassLoadListener> classLoadListeners = new ArrayList<>();
        List<AbstractProcessor<?>> processors = new ArrayList<>();


        for (Class<?> clazz : classList) {
            if (AbstractProcessor.class.isAssignableFrom(clazz)) {
                try {
                    AbstractProcessor<?> processor = (AbstractProcessor<?>) clazz.newInstance();
                    processors.add(processor);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }

            if (ClassLoadListener.class.isAssignableFrom(clazz)) {
                try {
                    ClassLoadListener classLoadListener = (ClassLoadListener) clazz.newInstance();
                    classLoadListeners.add(classLoadListener);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
        }

        boolean containsProcessor = false;
        StringBuilder conatinsMsg = new StringBuilder();
        for (AbstractProcessor<?> processor : processors) {
            try {
                if(getGameConsumer().containsProcessor(processor)){
                    containsProcessor = true;
                    conatinsMsg.append(processor.getClass().getSimpleName()).append("\n");
                }
            } catch (Exception e) {
//                String exceptionMsg = ExceptionUtils.getStackTrace(e);
//                Logs.DEFAULT_LOGGER.error(exceptionMsg);
                throw new RuntimeException(e);
            }
        }

        if(containsProcessor){
            response.writeAndFlush("<pre> contains processor : \n " + conatinsMsg.toString() + "</pre>");
            return;
        }


        StringBuilder sendHtmlSb = new StringBuilder();

        sendHtmlSb.append("<pre>");

        sendHtmlSb.append("load class num : ").append(classList.size()).append('\n');
        for (ClassLoadListener classLoadListener : classLoadListeners) {
            try {
                classLoadListener.beforeLoadProcessor(getGameConsumer());
                sendHtmlSb.append("beforeLoadProcessor execute listen : ").append(classLoadListener.getClass().getSimpleName()).append('\n');
            } catch (Exception e) {
                String exceptionMsg = ExceptionUtils.getStackTrace(e);
                sendHtmlSb.append("beforeLoadProcessor execute listen : ").append(classLoadListener.getClass().getSimpleName())
                        .append(" make exception : ").append(exceptionMsg).append('\n');
                Logs.DEFAULT_LOGGER.error(exceptionMsg);
            }
        }

        for (AbstractProcessor<?> processor : processors) {
            try {
                getGameConsumer().addProcessor(processor);
                sendHtmlSb.append("load processor : ").append(processor.getClass().getSimpleName()).append('\n');
            } catch (Exception e) {
                String exceptionMsg = ExceptionUtils.getStackTrace(e);
                sendHtmlSb.append("load processor : ").append(processor.getClass().getSimpleName())
                        .append(" make exception : ").append(exceptionMsg).append('\n');
                Logs.DEFAULT_LOGGER.error(exceptionMsg);
            }
        }


        for (ClassLoadListener classLoadListener : classLoadListeners) {

            try {
                classLoadListener.afterLoad(getGameConsumer());
                sendHtmlSb.append("after load execute listen : ").append(classLoadListener.getClass().getSimpleName()).append('\n');
            } catch (Exception e) {
                String exceptionMsg = ExceptionUtils.getStackTrace(e);
                sendHtmlSb.append("after load execute listen : ").append(classLoadListener.getClass().getSimpleName())
                        .append(" make exception : ").append(exceptionMsg).append('\n');
                Logs.DEFAULT_LOGGER.error(exceptionMsg);
            }
        }

        sendHtmlSb.append("</pre>");
        response.writeAndFlush(sendHtmlSb.toString());

    }
}
