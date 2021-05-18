package org.jyg.gameserver.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * create by jiayaoguang on 2020/9/5
 */
public class Logs {

    private Logs() {
    }

    public static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger("default");

    public static final Logger DB = LoggerFactory.getLogger("DB");


    public static final Logger CONSUMER = LoggerFactory.getLogger("consumer");

}
