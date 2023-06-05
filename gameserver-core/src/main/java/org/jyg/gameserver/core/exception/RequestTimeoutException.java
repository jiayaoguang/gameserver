package org.jyg.gameserver.core.exception;

/**
 * create by jiayaoguang on 2023/6/5
 */
public class RequestTimeoutException extends Exception{

    public RequestTimeoutException(String msg) {
        super(msg);
    }
}
