package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2020/7/12
 * 文本处理器
 */
public abstract class TextProcessor extends AbstractProcessor<String>{

    public TextProcessor() {
    }

    @Override
    public abstract void process(Session session, EventData<String> event);



}
