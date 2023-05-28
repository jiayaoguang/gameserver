package org.jyg.gameserver.core.event;

import java.util.List;

/**
 * create by jiayaoguang on 2023/5/21
 */
public class InvokeMethodEvent extends Event {



    private String methodUniqueName;


    private Object[] methodParams;

    public InvokeMethodEvent() {
    }



    public String getMethodUniqueName() {
        return methodUniqueName;
    }

    public void setMethodUniqueName(String methodUniqueName) {
        this.methodUniqueName = methodUniqueName;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }

    public void setMethodParams(Object[] methodParams) {
        this.methodParams = methodParams;
    }


}
