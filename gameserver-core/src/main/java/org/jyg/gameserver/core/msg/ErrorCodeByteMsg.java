package org.jyg.gameserver.core.msg;

import java.util.List;

/**
 * create by jiayaoguang at 2021/5/31
 */
public class ErrorCodeByteMsg implements ByteMsgObj{

    private int errorCode;
    private List<String> params;


    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }
}



